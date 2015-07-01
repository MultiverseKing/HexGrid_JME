package org.hexgridapi.editor.hexmap.gui;

import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.hexgridapi.core.mousepicking.GridMouseControlAppState;
import org.hexgridapi.core.ChunkCoordinate;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.hexgridapi.events.TileSelectionListener;

/**
 *
 * @author roah
 */
public class JCursorPositionPanel extends JPanel {
    
    private static HexCoordinate cursorPos;
    private JLabel cursorPosition = new JLabel("Hex pos : null");
    private JLabel chunkPosition = new JLabel("Chunk pos: null");

    public JCursorPositionPanel(GridMouseControlAppState system) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setAlignmentX(0);
        setBorder(BorderFactory.createTitledBorder("Cursor Property"));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        add(Box.createRigidArea(new Dimension(0, 3)));
        add(cursorPosition);
        add(Box.createRigidArea(new Dimension(0, 3)));
        add(chunkPosition);
        system.getSelectionControl().register(selectionListener);
    }
    
    private TileSelectionListener selectionListener = new TileSelectionListener() {
        @Override
        public void onTileSelectionUpdate(HexCoordinate currentSelection, ArrayList<HexCoordinate> selectedList) {
            cursorPos = currentSelection;
            cursorPosition.setText("Hex pos : " + currentSelection.toOffset());
            chunkPosition.setText("Chunk pos : " + ChunkCoordinate.getNewInstance(currentSelection));
        }
    };
    public HexCoordinate getPosition(){
        return cursorPos;
    }
}
