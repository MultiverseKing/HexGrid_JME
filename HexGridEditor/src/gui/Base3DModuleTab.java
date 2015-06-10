package gui;

import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;
import com.jme3.texture.Image;
import java.awt.Canvas;
import javax.swing.ImageIcon;

/**
 *
 * @author roah
 */
public abstract class Base3DModuleTab extends JPanelTab {

    public Base3DModuleTab(Image img, String name) {
        super(img, name);
    }

    public Base3DModuleTab(ImageIcon icon, String name) {
        super(icon, name);
    }

    @Override
    public final void isShow() {
    }

    @Override
    public final void isHidden() {
    }
    
    public abstract void onContextGainFocus(SimpleApplication app, Canvas canvas);
    /**
     * Detach the module node from the RootNode (detach everything from the rootNode)
     */
    public abstract void onContextLostFocus();
    public abstract Node getModuleNode();
}
