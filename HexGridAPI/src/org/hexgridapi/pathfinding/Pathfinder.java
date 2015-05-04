package org.hexgridapi.pathfinding;

import java.util.List;
import org.hexgridapi.core.MapData;
import org.hexgridapi.utility.HexCoordinate;

/**
 *
 * @author Eike Foede
 */
public interface Pathfinder {

    public void setMapData(MapData mapData);

    /**
     * Returns a shortest path between the two given HexCoordinates, if there is
     * any. Else returns null
     *
     * @param from
     * @param to
     * @return shortest path or null, if no path found
     */
    public List<HexCoordinate> getPath(HexCoordinate from, HexCoordinate to);
}
