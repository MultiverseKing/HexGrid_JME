package org.hexgridapi.core.geometry.builder;

import java.util.ArrayList;
import org.hexgridapi.core.data.procedural.ProceduralHexGrid;

/**
 *
 * @author roah
 */
public class GridParam {
    private final boolean onlyGround;
    private final boolean useVoidTile;
    private final ArrayList<String> textureKeys = new ArrayList<String>();
    private final ProceduralHexGrid generator;
    private final boolean useBuffer;

    /**
     * Define the parameter to use while the API is running. <br>
     * Does not activate the buffer (aka : infinite map). <br>
     * Throws {@link IllegalArgumentException} if : <br>
     * ({@param useBuffer} && {@param proceduralGen} == null && !{@param useVoidTile}) <br>
     * Throws {@link UnsupportedOperationException} if : <br>
     * ({@param useBuffer} && {@param chunkCoordinateType} !instanceOf {@link org.hexgridapi.core.control.buffercontrol})
     * 
     * @param textureKeys all used texture.
     * @param chunkCoordinateType Coordinate to use for chunk generation.
     * @param useVoidTile Generate or not Tile being null.
     * @param onlyGround Generate Chunk/Tile with depth.
     * @param useDefaultProcedural Use the API provided Procedural algorithm.
     */
    public GridParam(Object[] textureKeys, Class<? extends ChunkCoordinate> chunkCoordinateType, 
            boolean useVoidTile, boolean onlyGround, boolean useDefaultProcedural) {
        this(textureKeys, chunkCoordinateType, false, useVoidTile, onlyGround, 
                useDefaultProcedural ? new ProceduralHexGrid(textureKeys.length) : null);
    }
    
    /**
     * Define the parameter to use while the API is running. <br>
     * Throws {@link IllegalArgumentException} if : <br>
     * ({@param useBuffer} && {@param proceduralGen} == null && !{@param useVoidTile}) <br>
     * Throws {@link UnsupportedOperationException} if : <br>
     * ({@param useBuffer} && {@param chunkCoordinateType} !instanceOf {@link org.hexgridapi.core.control.buffercontrol})
     * 
     * @param textureKeys all used texture.
     * @param chunkCoordinateType Coordinate to use for chunk generation.
     * @param useBuffer Activate the buffer for the specifiate coordinate. (aka : infinite map)
     * @param useVoidTile Generate or not Tile being null.
     * @param onlyGround Generate Chunk/Tile with depth.
     * @param useDefaultProcedural Use the API provided Procedural algorithm.
     */
    public GridParam(Object[] textureKeys, Class<? extends ChunkCoordinate> chunkCoordinateType, 
            boolean useBuffer, boolean useVoidTile, boolean onlyGround, boolean useDefaultProcedural) {
        this(textureKeys, chunkCoordinateType, useBuffer, useVoidTile , onlyGround, 
                useDefaultProcedural ? new ProceduralHexGrid(textureKeys.length) : null);
    }
    
    /**
     * Define the parameter to use while the API is running. <br>
     * Throws {@link IllegalArgumentException} if : <br>
     * ({@param useBuffer} && {@param proceduralGen} == null && !{@param useVoidTile}) <br>
     * Throws {@link UnsupportedOperationException} if : <br>
     * ({@param useBuffer} && {@param chunkCoordinateType} !instanceOf {@link org.hexgridapi.core.control.buffercontrol})
     * 
     * 
     * @param textureKeys all used texture.
     * @param chunkCoordinateType Coordinate to use for chunk generation.
     * @param useBuffer Activate the buffer for the specifiate coordinate. (aka : infinite map)
     * @param useVoidTile Generate or not Tile being null.
     * @param onlyGround Generate Chunk/Tile with depth.
     * @param proceduralGen custom procedural algorithm to use.
     */
    public GridParam(Object[] textureKeys, Class<? extends ChunkCoordinate> chunkCoordinateType, 
            boolean useBuffer, boolean useVoidTile, boolean onlyGround, ProceduralHexGrid proceduralGen) {
        if(useBuffer && proceduralGen == null && !useVoidTile) {
            throw new IllegalArgumentException("Buffer cannot work without voidTile or ProceduralGen");
        }
        ChunkCoordinate.setCoordType(chunkCoordinateType);
        this.useBuffer = useBuffer;
        this.useVoidTile = useVoidTile;
        this.onlyGround = onlyGround;
        this.generator = proceduralGen;
        genTextureKeys(textureKeys);
    }

    private void genTextureKeys(Object[] userKey) {
        if (userKey != null) {
            for (int i = -1; i < userKey.length; i++) {
                if (i == -1) {
                    textureKeys.add("EMPTY_TEXTURE_KEY");
                } else {
                    if (userKey[i].toString().equals("NO_TILES")
                            || userKey[i].toString().equals("EMPTY_TEXTURE_KEY")
                            || userKey[i].toString().equals("SELECTION_TEXTURE")) {
                        throw new IllegalArgumentException(userKey[i] + " is not allowed.");
                    }
                    textureKeys.add(userKey[i].toString());
                }
            }
        } else {
            textureKeys.add("EMPTY_TEXTURE_KEY");
        }
    }

    boolean isOnlyGround() {
        return onlyGround;
    }

    boolean isUseVoidTile() {
        return useVoidTile;
    }

    boolean isUseBuffer() {
        return useBuffer;
    }

    public ArrayList<String> getTextureKeys() {
        return textureKeys;
    }

    public ProceduralHexGrid getGenerator() {
        return generator;
    }
}
