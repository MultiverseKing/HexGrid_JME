package org.hexgridapi.core;

import org.hexgridapi.core.coordinate.BufferBuilder;
import org.hexgridapi.core.geometry.buffer.BufferedChunk_old;
import org.hexgridapi.core.data.procedural.ProceduralHexGrid;
import org.hexgridapi.utility.Vector2Int;

/**
 * 
 * @author roah
 */
public class MapParam {
    private final boolean onlyGround;
    private final boolean buildVoidTile;
    private final int bufferSize;
    private final Vector2Int mapSize;
    private final boolean useProceduralGen;
    private final ProceduralHexGrid proceduralGenerator;
        
    /**
     * Define the parameter to use while the API is running. <br>
     * Throws {@link IllegalArgumentException} if : <br>
     * ({@param mapSize} == (0,0) && !{@param useProceduralGen} && !{@param buildVoidTile}) <br>
     * 
     * 
     * @param chunkCoordinateType Coordinate to use for chunk generation.
     * @param chunkSize How many tile a chunk contain.
     * @param mapSize Size of the map. (set to (0,0) for infinite map)
     * @param bufferSize radius the buffer will have. (must be >= 1)
     * @param buildVoidTile Generate or not Tile being null.
     * @param onlyGround Generate Chunk/Tile with depth.
     * @param useProceduralGen use procedural grid generation.
     * @param proceduralGen custom procedural algorithm to use.
     * @throws ClassCastException if ({@param mapSize} == (0,0))
     * &&  {@param chunkCoordinateType} is not assignable to {@link BufferBuilder}
     * @throws IllegalArgumentException if : ({@param mapSize} == (0,0)) 
     * && !{@param useProceduralGen} && !{@param buildVoidTile})
     * @throws UnsupportedOperationException if : ({@param mapSize} == (0,0)) 
     * && {@param chunkCoordinateType} !instanceOf {@link BufferedChunk_old})
     */
    public MapParam(Class<? extends ChunkCoordinate> chunkCoordinateType, Vector2Int mapSize,
            int chunkSize, int bufferSize, boolean buildVoidTile, boolean onlyGround, 
            boolean useProceduralGen, ProceduralHexGrid proceduralGen) {
        if(mapSize.equals(Vector2Int.ZERO) && !chunkCoordinateType.isInstance(BufferBuilder.class)){
            
        } else if(mapSize.equals(Vector2Int.ZERO) && !useProceduralGen && !buildVoidTile) {
            throw new IllegalArgumentException("Map Size cannot be equals to (0.0) "
                    + "without voidTile or ProceduralGen enabled.");
        }
        ChunkCoordinate.setCoordType(chunkCoordinateType, chunkSize);
        this.buildVoidTile = buildVoidTile;
        this.onlyGround = onlyGround;
        this.useProceduralGen = useProceduralGen;
        this.proceduralGenerator = proceduralGen;
        this.mapSize = mapSize;
        if(bufferSize < 1) {
            this.bufferSize = 1;
        } else if (mapSize.equals(Vector2Int.ZERO)) {
            this.bufferSize = 1;
        } else {
            this.bufferSize = bufferSize;
        }
    }

    public boolean isOnlyGround() {
        return onlyGround;
    }

    public boolean isBuildVoidTile() {
        return buildVoidTile;
    }

    public int getBufferRadius() {
        return bufferSize;
    }

    public Vector2Int getMapSize() {
        return mapSize;
    }

    public boolean isUsingProceduralGen() {
        return useProceduralGen;
    }

    public ProceduralHexGrid getProceduralGenerator() {
        return proceduralGenerator;
    }
}
