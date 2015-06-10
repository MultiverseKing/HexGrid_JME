package org.hexgridapi.core;

import com.jme3.math.FastMath;

/**
 * Contain parameters used to generate the room grid.
 *
 * @todo Chunk size change during runtime.
 * @todo Map load from any chunk Size. (saved map can only work with the
 * chunkSize they have been created) loader work.
 *
 * @author roah
 */
public final class HexSetting {

    /**
     * Radius to use when generating hex.
     */
    public final static float HEX_RADIUS = 1;
    /**
     * Width of a generated hex.
     */
    public final static float HEX_WIDTH = FastMath.sqrt(3) * 1; //FastMath.sqrt(3) * HEX_RADIUS;
    /**
     * Number of hex contain in a chunk.
     */
//    public final static GreddyMesher.ShapeType CHUNK_SHAPE_TYPE = GreddyMesher.ShapeType.HEXAGON;
    /**
     * Number of hex contain in a chunk.
     * if(GreddyMesher.ShapeType.HEXAGON) (this is the radius)
     * if(GreddyMesher.ShapeType.SQUARE)
     */
    public final static int CHUNK_SIZE = 18;
    /**
     * Ghost control size.
     */
//    public final static int GHOST_CONTROL_RADIUS = 1;
    /**
     * The initial depth given to any generated chunk.
     */
    public final static int WATER_LEVEL = 0; //must lesser than 0 if not 0
    /**
     * WU distance between two hex of different height.
     */
    public final static float FLOOR_OFFSET = .5f;
    /**
     * Used to know how many chunk to keep in memory before purging it.
     * Unused for the time being.
     *
     * @deprecated there is no data limit currently.
     */
    public final static int CHUNK_DATA_LIMIT = 4;
    /**
     * Path used to load the texture.
     */
    public final static String TEXTURE_PATH = "Textures/HexField/";
}
