package org.hexgridapi.core.data;

import org.hexgridapi.core.ChunkCoordinate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import org.hexgridapi.core.coordinate.HexCoordinate;

/**
 * Contain all user Hex data.
 *
 * @author roah
 */
public class ChunkData {

    /**
     * Map holding all chunk on the current memory.
     */
    private final HashMap<ChunkCoordinate, HashMap> chunks = new HashMap<ChunkCoordinate, HashMap>();

    HexTile add(HexCoordinate tilePos, HexTile tile) {
        if (!chunks.containsKey(ChunkCoordinate.getNewInstance(tilePos))) {
            chunks.put(ChunkCoordinate.getNewInstance(tilePos), new HashMap<HexCoordinate, HexTile>());
        }
        return (HexTile) chunks.get(ChunkCoordinate.getNewInstance(tilePos)).put(tilePos, tile);
    }

    /**
     * Remove a tile if it exist.
     *
     * @param tilePos
     * @return old Contained tile.
     */
    HexTile remove(HexCoordinate tilePos) {
        if (chunks.containsKey(ChunkCoordinate.getNewInstance(tilePos))) {
            return (HexTile) chunks.get(ChunkCoordinate.getNewInstance(tilePos)).remove(tilePos);
        }
        return null;
    }

    /**
     * Return Hextile properties if it exist otherwise return null.
     *
     * @param tilePos tilePos inside the chunk.
     * @return null if the tile doesn't exist.
     */
    HexTile getTile(HexCoordinate tilePos) {
        if (chunks.containsKey(ChunkCoordinate.getNewInstance(tilePos))) {
            return (HexTile) chunks.get(ChunkCoordinate.getNewInstance(tilePos)).get(tilePos);
        }
        return null;
    }

    boolean exist(ChunkCoordinate chunk, HexCoordinate tilePos) {
        if (chunks.containsKey(chunk)) {
            if (chunks.get(chunk).containsKey(tilePos.toOffset())) {
                return true;
            }
        }
        return false;
    }

    Collection getChunkTiles(ChunkCoordinate chunkPos) {
        return Collections.unmodifiableCollection(chunks.get(chunkPos).values());
    }
    
    /**
     * Check if the specifiate chunk is currently stored.
     *
     * @param chunkPos inspected chunk.
     * @return true if stored.
     */
    boolean contain(ChunkCoordinate chunkPos) {
        return chunks.containsKey(chunkPos);
    }

    /**
     * Check if the specifiate tile is currently stored.
     *
     * @param tilePos inspected tile.
     * @return true if stored.
     */
    boolean contain(HexCoordinate tilePos) {
        if (contain(ChunkCoordinate.getNewInstance(tilePos))) {
            return chunks.get(ChunkCoordinate.getNewInstance(tilePos)).containsKey(tilePos);
        }
        return false;
    }

    void clear() {
        chunks.clear();
    }

    boolean isEmpty() {
        return chunks.isEmpty();
    }
}
