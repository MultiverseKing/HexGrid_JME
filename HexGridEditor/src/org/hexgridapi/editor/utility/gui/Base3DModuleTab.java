package org.hexgridapi.editor.utility.gui;

import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;
import com.jme3.texture.Image;
import java.awt.Canvas;

/**
 *
 * @author roah
 */
public abstract class Base3DModuleTab extends JPanelTab {
    
    protected Node moduleNode;
    protected boolean useDefaultSettings;

    /**
     * 
     * @param img image used as icon for the tab.
     * @param name title of the tab
     * @param moduleNode rootNode of the module.
     * This will set useDefaultSettings to false.
     */
    public Base3DModuleTab(Image img, String name, Node moduleNode) {
        this(img, name, moduleNode, false);
    }
    
    /**
     * 
     * @param img image used as icon for the tab.
     * @param name title of the tab
     * @param moduleNode rootNode of the module.
     * @param useDefaultSettings use the editor helper param for lighting ?
     */
    public Base3DModuleTab(Image img, String name, Node moduleNode, boolean useDefaultSettings) {
        super(img, name);
        this.moduleNode = moduleNode;
        this.useDefaultSettings = useDefaultSettings;
    }

    @Override
    public final void isShow() {
    }

    @Override
    public final void isHidden() {
    }
    /**
     * Main Node used by the module.
     */
    public Node getModuleNode(){
        return moduleNode;
    }
    /**
     * Some helpers to configure the scene like lighting...
     */
    public boolean useDefaultSettings(){
        return useDefaultSettings;
    }
    
    /**
     * Called before attaching the module node to the RootNode
     * @param canvas have to be added manually to the panel.
     */
    public abstract void onContextGainFocus(SimpleApplication app, Canvas canvas);
    /**
     * Called before detaching the module node from the RootNode 
     * (everything is detach from the rootNode also is the canvas from the panel)
     */
    public abstract void onContextLostFocus();
}
