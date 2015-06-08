package hexmap.gui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import hexmap.HexMapModule;
import java.awt.Frame;

/**
 *
 * @author roah
 */
public final class JHexEditorMenu extends JMenu {

    private final HexMapModule module;
    
    public JHexEditorMenu(HexMapModule module) {
        super("HexGrid");
        this.module = module;
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
                module.generateNewMap();
                if (!module.isStart()) {
                    setAction(HexMenuAction.Save);
                }
                break;
            case "Load Map":
                JLoaderDialog loadDialog = new JLoaderDialog((Frame) module.getTopLevelAncestor(), false);
                loadDialog.setLocationRelativeTo((Frame) module.getTopLevelAncestor());
                loadDialog.setVisible(true);

                String loadName = loadDialog.getValidatedText();
                if (loadName != null) {
                    //The text is valid.
                    System.err.println("@todo Load Map " + loadName);
                }
                loadDialog.dispose();
                break;
            case "Save Map":
                JLoaderDialog saveDialog = new JLoaderDialog((Frame) module.getTopLevelAncestor(),
                        module.getMapName());
                saveDialog.setLocationRelativeTo((Frame) module.getTopLevelAncestor());
                saveDialog.setModal(true);
                saveDialog.setVisible(true);
                String saveName = saveDialog.getValidatedText();
                if (saveName != null) {
                    //The text is valid.
                    module.saveMap();
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
