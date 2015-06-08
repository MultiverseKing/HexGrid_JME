package org.hexgridapi.core.geometry.builder.ghost;

import org.hexgridapi.core.geometry.builder.ChunkCoordinate;

/**
 *
 * @author roah
 * @deprecated since GhostChunkBuilder is deprecated
 */
public class GhostControlCullingData {
    private ChunkCoordinate coord;
    private boolean cull;

    public GhostControlCullingData(ChunkCoordinate coord, boolean cull) {
        this.coord = coord;
        this.cull = cull;
    }

    public ChunkCoordinate getCoord() {
        return coord;
    }

    public boolean isCull() {
        return cull;
    }
}
