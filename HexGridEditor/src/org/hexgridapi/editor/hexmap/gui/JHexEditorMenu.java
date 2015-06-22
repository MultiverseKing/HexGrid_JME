package org.hexgridapi.editor.hexmap.gui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import org.hexgridapi.editor.hexmap.HexMapModule;
import java.awt.Frame;
import org.hexgridapi.core.geometry.builder.GridParam;

/**
 *
 * @author roah
 */
public final class JHexEditorMenu extends JMenu {

    private final HexMapModule hexmap;
    
    public JHexEditorMenu(HexMapModule hexMap) {
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
                
                GridParam param = newMapDialog.getValidatedParam();
                if(param != null) {
                    hexmap.generateNewMap(param);
                }
                if (!hexmap.isStart()) { //@todo
                    setAction(HexMenuAction.Save);
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
                        hexmap.getMapName());
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
