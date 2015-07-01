package org.hexgridapi.editor.utility.gui;

import com.jme3.texture.Image;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import org.hexgridapi.editor.utility.ImageConverter;

/**
 *
 * @author roah
 */
public abstract class JPanelTab extends JPanel {

    private ImageIcon imgIcon;

    public JPanelTab(Image img, String name) {
        this(ImageConverter.convertToIcon(img, 16, 16), name);
    }

    public JPanelTab(ImageIcon icon, String name) {
        this.imgIcon = icon;
        super.setName(name.replaceAll("\\s+",""));
        
        setBorder(BorderFactory.createTitledBorder(name));
        setPreferredSize(new Dimension(170, 300));
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        addWithSpace(separator);
        
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
//        setMinimumSize(new Dimension(15, 15));
//        setAlignmentX(0);
    }

    public ImageIcon getIcon() {
        return imgIcon;
    }

    protected final void addWithSpace(Component comp) {
        addComp(null, comp);
    }

    protected void addComp(JPanel pan, Component comp) {
        if (pan != null) {
            pan.add(Box.createRigidArea(new Dimension(0, 2)));
            pan.add(comp);
        } else {
            add(Box.createRigidArea(new Dimension(0, 2)));
            add(comp);
        }
    }

    public abstract void isShow();
    public abstract void isHidden();
}
