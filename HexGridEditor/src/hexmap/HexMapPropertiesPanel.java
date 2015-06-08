package hexmap;

import gui.JPanelTab;
import hexmap.gui.JCursorPositionPanel;
import javax.swing.ImageIcon;
import org.hexgridapi.core.appstate.MouseControlSystem;

/**
 *
 * @author roah
 */
public class HexMapPropertiesPanel extends JPanelTab {

    HexMapPropertiesPanel(ImageIcon icon, JCursorPositionPanel cursor) {
        super(icon, "HexMap");
//        add(new JCursorPositionPanel(mouseSystem));
        add(cursor);
    }

    @Override
    public void isShow() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void isHidden() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
