package org.hexgridapi.editor.utility.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

/**
 *
 * @author roah
 */
public abstract class ExtendedJDialog extends JDialog implements ActionListener, PropertyChangeListener {
    private String btnString1 = "Enter";
    private String btnString2 = "Cancel";
    private JOptionPane optionPane;
    
    public ExtendedJDialog(Frame owner, boolean modal) {
        super(owner, modal);
    }

    public ExtendedJDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
    }

    @Override
    public void setVisible(boolean b) {
        init();
        super.setVisible(b);
    }

    private void init(){
        //Create an array specifying the number of dialog buttons
        //and their text.
        Object[] options = {btnString1, btnString2};
        setMinimumSize(new Dimension(350, 150));

        //Create the JOptionPane.
        optionPane = new JOptionPane(getMessage(),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.YES_NO_OPTION,
                null,
                options,
                options[0]);
        
        //Make this dialog display it.
        setContentPane(optionPane);

        //Handle window closing correctly.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                /*
                 * Instead of directly closing the window,
                 * we're going to change the JOptionPane's
                 * value property.
                 */
                optionPane.setValue(new Integer(
                        JOptionPane.CLOSED_OPTION));
            }
        });

        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
        
        pack();
        setLocationRelativeTo(getOwner());
    }

    /**
     * This method handles events for the text field.
     */
    @Override
    public final void actionPerformed(ActionEvent e) {
        optionPane.setValue(btnString1);
    }

    /**
     * This method reacts to state changes in the option pane.
     */
    @Override
    public final void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (isVisible()
                && (e.getSource() == optionPane)
                && (JOptionPane.VALUE_PROPERTY.equals(prop)
                || JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            Object value = optionPane.getValue();

            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                //ignore reset
                return;
            }

            //Reset the JOptionPane's value.
            //If you don't do this, then if the user
            //presses the same button next time, no
            //property change event will be fired.
            optionPane.setValue(
                    JOptionPane.UNINITIALIZED_VALUE);

            if (btnString1.equals(value)) {
                if(userValidated()) {
                    clearAndHide();
                }
            } else {
                userCancelled();
                clearAndHide();
            }
        }
    }

    private void clearAndHide() {
        dialogClose();
        setVisible(false);
    }
    
    /**
     * What you want to show inside the Dialog.
     */
    protected abstract Object[] getMessage();

    /**
     * if(true) we're done; clear and dismiss the dialog
     * if(false) keep the dialog; do nothing
     */
    protected abstract boolean userValidated();

    /**
     * User closed dialog or clicked cancel.
     */
    protected abstract void userCancelled();
    /**
     * Dialog is removed from screen use this to clear it.
     */
    protected abstract void dialogClose();
}
