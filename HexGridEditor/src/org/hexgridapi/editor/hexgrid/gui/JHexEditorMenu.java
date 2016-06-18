package org.hexgridapi.editor.hexgrid.gui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import org.hexgridapi.editor.hexgrid.HexGridModule;
import java.awt.Frame;
import org.hexgridapi.core.MapParam;

/**
 *
 * @author roah
 */
public final class JHexEditorMenu extends JMenu {

    private final HexGridModule hexmap;
    
    public JHexEditorMenu(HexGridModule hexMap) {
        super("HexGrid");
        this.hexmap = hexMap;
    }

    public void setAction(HexMenuAction action) {
        String name = action.toString() + " Map";
        add(new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAction(e);
            }
        });
    }

    public void onAction(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "New Map":
                JNewMapDialog newMapDialog = new JNewMapDialog((Frame) getTopLevelAncestor());
                newMapDialog.setVisible(true);
                
                MapParam param = newMapDialog.getValidatedParam();
                if(param != null) {
                    hexmap.generateNewMap(param);
                }
                break;
            case "Load Map":
                JLoaderDialog loadDialog = new JLoaderDialog((Frame) getTopLevelAncestor(), false);
                loadDialog.setVisible(true);

                String loadName = loadDialog.getValidatedText();
                if (loadName != null) {
                    hexmap.LoadMap(loadName);
                }
                loadDialog.dispose();
                break;
            case "Save Map":
                JLoaderDialog saveDialog = new JLoaderDialog((Frame) getTopLevelAncestor(),
                        hexmap.getHexGridSystem().getMapName());
                saveDialog.setVisible(true);
                String saveName = saveDialog.getValidatedText();
                if (saveName != null) {
                    hexmap.saveMap(saveName);
                }
                saveDialog.dispose();
                break;
            default:
                System.err.println("No associated action for : " + e.getActionCommand());
        }
    }

    public enum HexMenuAction {

        New,
        Load,
        Save;
    }
}
