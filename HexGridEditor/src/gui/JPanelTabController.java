package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/**
 *
 * @author roah
 */
public class JPanelTabController extends JPanel {

    private final JPanel iconPan;
    private final ButtonGroup iconGrp = new ButtonGroup();
    private HashMap<String, JPanelTab> panels = new HashMap<>();
    private ArrayList<JPanelTabListener> listeners = new ArrayList<>();
    private boolean initialized = false;

    public JPanelTabController(String name) {
//        setName("HexPropertiesPanelHolder");
        setName(name);
//        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setLayout(new BorderLayout());
//        setAlignmentX(0);
        setPreferredSize(new Dimension(170, Integer.MAX_VALUE));
        
        iconPan = new JPanel();
        buildNorthHolder();
        validate();
    }

    private void buildNorthHolder() {
        JPanel northHolder = new JPanel();
        northHolder.setLayout(new BoxLayout(northHolder, BoxLayout.PAGE_AXIS));
        iconPan.setBorder(BorderFactory.createBevelBorder(-1));//createLineBorder(Color.BLACK));
        iconPan.setLayout(new BoxLayout(iconPan, BoxLayout.LINE_AXIS));
//        iconPan.setAlignmentX(0);
        iconPan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
//        iconPan.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));

//        iconPan.add(Box.createRigidArea(new Dimension(0, 2)));
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        northHolder.add(Box.createRigidArea(new Dimension(0, 1)));
        northHolder.add(iconPan);
        northHolder.add(Box.createRigidArea(new Dimension(0, 1)));
        northHolder.add(separator);
        
        add(northHolder, BorderLayout.NORTH);
    }

    public void add(JPanelTab panel) {
        if (!panels.containsKey(panel.getName())) {
            JButton buttonIco = new JButton(panel.getIcon());
            buttonIco.setName(panel.getName());
            buttonIco.addActionListener(new AbstractAction(panel.getName()) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!iconGrp.getSelection().equals(((JButton) e.getSource()).getModel())) {
                        updatePanel(getValue(Action.NAME).toString());
                    }
                }
            });
            buttonIco.setPreferredSize(new Dimension(15, 15));
            buttonIco.setMaximumSize(new Dimension(20, 20));
            buttonIco.setBorder(BorderFactory.createEmptyBorder());
            buttonIco.setContentAreaFilled(false);
            iconPan.add(buttonIco);
            iconGrp.add(buttonIco);
            panels.put(panel.getName(), panel);
            if (!initialized) {
                super.add(panel, BorderLayout.CENTER);
                iconGrp.setSelected(buttonIco.getModel(), true);
                buttonIco.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                panel.isShow();
                validate();
//                iconPan.validate();
                initialized = true;
                updateListeners(panel);
            } else {
                iconPan.revalidate();
//                iconPan.repaint();
            }
        }
    }

    private void updatePanel(String newPan) {
        for (Component c : this.getComponents()) {
            if (c instanceof JPanel && !((JPanel) c).equals(iconPan)) {
                panels.get(c.getName()).isHidden();
                this.remove(panels.get(c.getName()));
                break;
            }
        }
        super.add(panels.get(newPan));
        panels.get(newPan).isShow();
        updateListeners(panels.get(newPan));

        for (Component b : iconPan.getComponents()) {
            if (((JButton) b).getName().equals(newPan)) {
                ((JButton) b).setBorder(BorderFactory.createLineBorder(Color.BLACK));
                iconGrp.setSelected(((JButton) b).getModel(), true);
            } else {
                ((JButton) b).setBorder(BorderFactory.createEmptyBorder());
            }
        }

        revalidate();
        repaint();
    }

    public void registerTabChangeListener(JPanelTabListener listener) {
        listeners.add(listener);
    }

    public void removeTabChangeListener(JPanelTabListener listener) {
        listeners.remove(listener);
    }

    private void updateListeners(JPanelTab tab) {
        for (JPanelTabListener l : listeners) {
            l.onPanelChange(tab);
        }
    }
}
