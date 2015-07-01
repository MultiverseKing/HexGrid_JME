package org.hexgridapi.editor.hexmap.gui;

import com.jme3.texture.Image;
import javax.swing.ImageIcon;
import org.hexgridapi.editor.utility.gui.JPanelTab;

/**
 *
 * @author roah
 */
public abstract class HexGridPropertiesPan extends JPanelTab {

    public HexGridPropertiesPan(Image img, String name) {
        super(img, name);
    }

    public HexGridPropertiesPan(ImageIcon icon, String name) {
        super(icon, name);
    }

    public abstract void onMapLoaded();
    public abstract void onMapReset();
    public abstract void onMapRemoved();
}
