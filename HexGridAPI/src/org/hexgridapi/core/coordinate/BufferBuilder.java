package org.hexgridapi.core.coordinate;

import com.jme3.scene.Mesh;
import java.util.List;
import org.hexgridapi.core.ChunkCoordinate;

/**
 * Interface used to define the behaviors of the buffer.
 * 
 * @author roah
 */
public interface BufferBuilder {

    /**
     * Used by the BufferBuilder to get all chunk it need to render.
     * 
     * @param bufferCoord position where the buffer is currently.
     * @param bufferRadius radius of the buffer from the center.
     * @return list of all chunk arround the center of the buffer in the specifiated radius.
     */
    List<ChunkCoordinate> getBufferedChunk(ChunkCoordinate bufferCoord, int bufferRadius);

    /**
     * The plane used to collide with the camera Raycast.
     * 
     * @param bufferRadius radius of the buffer from the center.
     * @deprecated unneeded as the raycast always hit at center...
     */
    Mesh genCollisionPlane(int bufferRadius);
}
