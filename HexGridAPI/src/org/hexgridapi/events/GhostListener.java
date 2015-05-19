package org.hexgridapi.events;

import org.hexgridapi.utility.Vector2Int;

/**
 * Used with the Ghost Control to get the latest update from it.
 *
 * @author roah
 */
public interface GhostListener {

    void positionUpdate(Vector2Int newChunkCenterPosition);
}
