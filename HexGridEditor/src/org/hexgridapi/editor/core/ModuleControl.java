package org.hexgridapi.editor.core;

import com.jme3.scene.Node;
import com.jme3.system.JmeCanvasContext;
import java.awt.Canvas;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.Callable;
import javax.swing.JPanel;
import org.hexgridapi.editor.utility.gui.Base3DModuleTab;
import org.hexgridapi.editor.utility.gui.JPanelTab;
import org.hexgridapi.editor.utility.gui.JPanelTabController;
import org.hexgridapi.editor.utility.gui.JPanelTabListener;
import org.hexgridapi.utility.Vector2Int;

/**
 *
 * @author roah
 */
public final class ModuleControl implements JPanelTabListener {

    private final JPanelTabController tabController = new JPanelTabController("ModuleControl");
    private final EditorMain app;
    private final Canvas canvas;
    private final Node rootNode;
    private final DefaultSetting defaultSetting;
    private Base3DModuleTab currentModule;

    ModuleControl(EditorMain app) {
        this.app = app;
        rootNode = app.getRootNode();
        canvas = ((JmeCanvasContext) app.getContext()).getCanvas();
        defaultSetting = new DefaultSetting(app);

        canvasResizer();

//        tabController.setPreferredSize(canvas.getSize());
        tabController.registerTabChangeListener(this);
    }

    private void canvasResizer() {
        canvas.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                final Vector2Int dim = new Vector2Int(e.getComponent().getWidth(), e.getComponent().getHeight());
                app.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        if (currentModule != null) {
                            app.getCamera().resize(dim.x, dim.y, true);
                        }
                        return null;
                    }
                });
            }
        });
    }

    JPanel getContent() {
        return tabController;
    }

    /**
     * Add a root Tab to the Editor.
     *
     * @see javax.swing.JTabbedPane#addTab
     */
    public void addRootTab(JPanelTab tab) {
        tabController.add(tab);
    }

    @Override
    public void onPanelChange(final JPanelTab tab) {
        if (tab instanceof Base3DModuleTab) {
            if (currentModule != null) {
                currentModule.onContextLostFocus();
                currentModule.remove(canvas);

                app.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        rootNode.detachAllChildren();
//                        if (((Base3DModuleTab) tab).useDefaultSettings()) {
//                            defaultSetting.removeLight();
//                        }
                        return null;
                    }
                });
            }

            app.enqueue(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    if (((Base3DModuleTab) tab).useDefaultSettings() && !defaultSetting.isLightEnabled()) {
                        defaultSetting.addLight();
                    } else if (!((Base3DModuleTab) tab).useDefaultSettings() && defaultSetting.isLightEnabled()) {
                        defaultSetting.removeLight();
                    }
                    rootNode.attachChild(((Base3DModuleTab) tab).getModuleNode());
                    ((Base3DModuleTab) tab).onContextGainFocus(app, canvas);
                    return null;
                }
            });
//            canvas.setSize(((Base3DModuleTab) tab).getSize());
            app.getRootFrame().revalidate();
            app.getRootFrame().repaint();
            currentModule = (Base3DModuleTab) tab;
        }
    }
}
