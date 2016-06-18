package org.hexgridapi.editor.hexgrid.gui;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.*;
import java.awt.event.*;
import org.hexgridapi.editor.utility.gui.ExtendedJDialog;

/* 1.4 example used by DialogDemo.java. */
public class JLoaderDialog extends ExtendedJDialog {

    private String typedText = null;
    private JTextField textField;
//    private DialogDemo dd;
    private String[] protectedName = new String[]{"TEMP", "DEFAULT"};
    private boolean isSave;
    private String defaultValue;

    /**
     * Used when saving.
     */
    public JLoaderDialog(Frame aFrame, String defaultValue) {
        this(aFrame, true, defaultValue);
    }

    public JLoaderDialog(Frame aFrame, boolean isSave) {
        this(aFrame, isSave, null);
    }

    public JLoaderDialog(Frame aFrame, boolean isSave, String defaultValue) {
        super(aFrame, isSave ? "Save Map" : "Load Map", true);
        textField = new JTextField(10);

        this.isSave = isSave;
        this.defaultValue = defaultValue;

        //Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent ce) {
                textField.requestFocusInWindow();
            }
        });

        setMinimumSize(new Dimension(350, 150));

        //Register an event handler that puts the text into the option pane.
        textField.addActionListener(this);
    }

    @Override
    protected Object[] getMessage() {
        //Create an array of the text and components to be displayed.
        Object[] msg = new Object[2];
        if (isSave) {
            msg[0] = "Name to use for save ?";
            if (defaultValue != null) {
                textField.setText(defaultValue);
            }
        } else {
            msg[0] = "Map name to load ?";
        }
        msg[1] = textField;
        return msg;
    }

    @Override
    protected boolean userValidated() {
        typedText = textField.getText();
        String ucText = typedText.toUpperCase();
        //@todo check if the map already exist and if override
        //@todo check if the map exist before loading
        if (!protectedName[0].equals(ucText) && !protectedName[1].equals(ucText)) {
            return true;
        } else {
            //text was invalid
            textField.selectAll();
            JOptionPane.showMessageDialog(
                    JLoaderDialog.this,
                    "Sorry, \"" + typedText + "\" "
                    + "isn't a valid name.\n",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            typedText = null;
            textField.requestFocusInWindow();
            return false;
        }
    }

    @Override
    protected void userCancelled() {
        typedText = null;
    }

    /**
     * Returns null if the typed string was invalid;
     * otherwise, returns the string as the user entered it.
     */
    public String getValidatedText() {
        return typedText;
    }
    
    @Override
    public void dialogClose() {
        textField.setText(null);
    }
}