package org.hexgridapi.core.data.procedural;

import java.util.HashMap;
import org.hexgridapi.utility.Vector2Int;

/**
 * used as chunk Holder while generating procedural content.
 *
 * @author roah
 */
public class ProceduralChunkData {

    private final HashMap<Integer, HashMap<Vector2Int, Float>> chunkData;

    public ProceduralChunkData(HashMap<Integer, HashMap<Vector2Int, Float>> chunkData) {
        this.chunkData = chunkData;
    }
    
    public float getData(int param, Vector2Int locPosition) {
        return chunkData.get(param).get(locPosition);
    }
}
