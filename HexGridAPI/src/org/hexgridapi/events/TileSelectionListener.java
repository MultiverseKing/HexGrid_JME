package org.hexgridapi.events;

import java.util.ArrayList;
import org.hexgridapi.core.coordinate.HexCoordinate;

/**
 *
 * @author roah
 */
public interface TileSelectionListener {

    void onTileSelectionUpdate(HexCoordinate currentSelection, ArrayList<HexCoordinate> selectedList);
}
