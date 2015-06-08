package core;

import gui.Base3DModule;
import com.jme3.scene.Node;
import com.jme3.system.JmeCanvasContext;
import gui.JPanelTab;
import gui.JPanelTabController;
import gui.JPanelTabListener;
import java.awt.Canvas;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.Callable;
import javax.swing.JPanel;
import org.hexgridapi.utility.Vector2Int;

/**
 *
 * @author roah
 */
public final class ModuleControlTab implements JPanelTabListener {

    private static JPanelTabController tabController = new JPanelTabController("RootModulePanelControl");
    private static HexGridEditorMain app;
    private static Canvas canvas;
    private static Node rootNode;
    private Base3DModule currentModule;

    ModuleControlTab(HexGridEditorMain app) {
        ModuleControlTab.rootNode = app.getRootNode();
        ModuleControlTab.app = app;
        ModuleControlTab.canvas = ((JmeCanvasContext) app.getContext()).getCanvas();
        
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
                        if(currentModule != null){
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
    public void onPanelChange(JPanelTab tab) {
        if (tab instanceof Base3DModule) {
            if (currentModule != null) {
                currentModule.onContextLostFocus();
                currentModule.remove(canvas);
                currentModule.getModuleNode().removeFromParent();
            }
            rootNode.attachChild(((Base3DModule) tab).getModuleNode());
            ((Base3DModule) tab).onContextGainFocus(app, canvas);
//            canvas.setSize(((Base3DModule) tab).getSize());
            app.getRootFrame().revalidate();
            currentModule = (Base3DModule) tab;
        }
    }
}
