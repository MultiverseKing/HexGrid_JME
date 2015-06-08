package org.hexgridapi.events;

import org.hexgridapi.core.geometry.builder.ChunkCoordinate;

/**
 * This can be used by any class to get the latest update from Buffer Control.
 *
 * @author roah
 */
public interface BufferListener {

    /**
     * Used to get the buffer position.
     * The position is correspond to the buffer center.
     * 
     * @param newBufferPosition current position of the buffer.
     */
    void positionUpdate(ChunkCoordinate newBufferPosition);
}
