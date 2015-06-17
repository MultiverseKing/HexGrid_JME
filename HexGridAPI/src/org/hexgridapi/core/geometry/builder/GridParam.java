package org.hexgridapi.core.geometry.builder;

import java.util.ArrayList;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.data.procedural.ProceduralHexGrid;

/**
 *
 * @author roah
 */
public class GridParam {
    private final boolean onlyGround;
    private final boolean buildVoidTile;
    private final ArrayList<String> textureKeys = new ArrayList<String>();
    private final ProceduralHexGrid generator;
    private final boolean useBuffer;
    private final String texturePath;

    /**
     * Define the parameter to use while the API is running. <br>
     * Does not activate the infinite map. <br>
     * Throws {@link IllegalArgumentException} if : <br>
     * ({@param useBuffer} && {@param proceduralGen} == null && !{@param buildVoidTile}) <br>
     * Throws {@link UnsupportedOperationException} if : <br>
     * ({@param useBuffer} && {@param chunkCoordinateType} !instanceOf {@link org.hexgridapi.core.control.buffercontrol})
     * 
     * @param texturePath location of the texture folder. (by default it's Textures/Hexfield/)
     * @param textureKeys all used texture.
     * @param chunkCoordinateType Coordinate to use for chunk generation.
     * @param buildVoidTile Generate or not Tile being null.
     * @param onlyGround Generate Chunk/Tile with depth.
     * @param useDefaultProcedural Use the API provided Procedural algorithm.
     */
    public GridParam(String texturePath, Object[] textureKeys, Class<? extends ChunkCoordinate> chunkCoordinateType, 
            boolean buildVoidTile, boolean onlyGround, boolean useDefaultProcedural) {
        this(null, textureKeys, chunkCoordinateType, false, buildVoidTile, onlyGround, 
                useDefaultProcedural ? new ProceduralHexGrid(textureKeys.length) : null);
    }
    
    /**
     * Define the parameter to use while the API is running. <br>
     * Throws {@link IllegalArgumentException} if : <br>
     * ({@param useBuffer} && {@param proceduralGen} == null && !{@param buildVoidTile}) <br>
     * Throws {@link UnsupportedOperationException} if : <br>
     * ({@param useBuffer} && {@param chunkCoordinateType} !instanceOf {@link org.hexgridapi.core.control.buffercontrol})
     * 
     * @param texturePath location of the texture folder. (by default it's Textures/Hexfield/)
     * @param textureKeys all used texture.
     * @param chunkCoordinateType Coordinate to use for chunk generation.
     * @param useBuffer Activate the infinite map for the specifiate coordinate.
     * @param buildVoidTile Generate or not Tile being null.
     * @param onlyGround Generate Chunk/Tile with depth.
     * @param useDefaultProcedural Use the API provided Procedural algorithm.
     */
    public GridParam(String texturePath, Object[] textureKeys, Class<? extends ChunkCoordinate> chunkCoordinateType, 
            boolean useBuffer, boolean buildVoidTile, boolean onlyGround, boolean useDefaultProcedural) {
        this(texturePath, textureKeys, chunkCoordinateType, useBuffer, buildVoidTile , onlyGround, 
                useDefaultProcedural ? new ProceduralHexGrid(textureKeys.length) : null);
    }
    
    /**
     * Define the parameter to use while the API is running. <br>
     * Throws {@link IllegalArgumentException} if : <br>
     * ({@param useBuffer} && {@param proceduralGen} == null && !{@param buildVoidTile}) <br>
     * Throws {@link UnsupportedOperationException} if : <br>
     * ({@param useBuffer} && {@param chunkCoordinateType} !instanceOf {@link org.hexgridapi.core.control.buffercontrol})
     * 
     * 
     * @param textureKeys all used texture.
     * @param chunkCoordinateType Coordinate to use for chunk generation.
     * @param useBuffer Activate the buffer for the specifiate coordinate. (aka : infinite map)
     * @param buildVoidTile Generate or not Tile being null.
     * @param onlyGround Generate Chunk/Tile with depth.
     * @param proceduralGen custom procedural algorithm to use.
     */
    public GridParam(String texturePath, Object[] textureKeys, Class<? extends ChunkCoordinate> chunkCoordinateType, 
            boolean useBuffer, boolean buildVoidTile, boolean onlyGround, ProceduralHexGrid proceduralGen) {
        if(useBuffer && proceduralGen == null && !buildVoidTile) {
            throw new IllegalArgumentException("Buffer cannot work without voidTile or ProceduralGen");
        }
        ChunkCoordinate.setCoordType(chunkCoordinateType);
        this.useBuffer = useBuffer;
        this.buildVoidTile = buildVoidTile;
        this.onlyGround = onlyGround;
        this.texturePath = texturePath != null ? texturePath : "Textures/HexField/";
        this.generator = proceduralGen;
        genTextureKeys(textureKeys);
    }

    private void genTextureKeys(Object[] userKey) {
        textureKeys.add(MapData.DefaultTextureValue.EMPTY_TEXTURE_KEY.toString());
        if (userKey != null) {
            for (int i = 0; i < userKey.length; i++) {
                if (userKey[i].toString().equals(MapData.DefaultTextureValue.NO_TILE.toString())
                        || userKey[i].toString().equals(MapData.DefaultTextureValue.EMPTY_TEXTURE_KEY.toString())
                        || userKey[i].toString().equals(MapData.DefaultTextureValue.SELECTION_TEXTURE.toString())) {
                    throw new IllegalArgumentException(userKey[i] + " is not allowed.");
                }
                textureKeys.add(userKey[i].toString());
            }
        }
    }

    public boolean isOnlyGround() {
        return onlyGround;
    }

    public boolean isBuildVoidTile() {
        return buildVoidTile;
    }

    public boolean isUseBuffer() {
        return useBuffer;
    }

    public ArrayList<String> getTextureKeys() {
        return textureKeys;
    }

    public ProceduralHexGrid getGenerator() {
        return generator;
    }

    public String getTexturePath() {
        return texturePath;
    }
}
