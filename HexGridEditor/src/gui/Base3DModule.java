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
public abstract class Base3DModule extends JPanelTab {

    public Base3DModule(Image img, String name) {
        super(img, name);
    }

    public Base3DModule(ImageIcon icon, String name) {
        super(icon, name);
    }

    @Override
    public final void isShow() {
    }

    @Override
    public final void isHidden() {
    }
    
    public abstract void onContextGainFocus(SimpleApplication app, Canvas canvas);
    public abstract void onContextLostFocus();
    public abstract Node getModuleNode();
}
