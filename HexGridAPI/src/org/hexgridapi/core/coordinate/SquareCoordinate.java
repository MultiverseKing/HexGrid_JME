package org.hexgridapi.core.coordinate;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.util.ArrayList;
import java.util.List;
import org.hexgridapi.core.ChunkCoordinate;
import org.hexgridapi.core.geometry.HexSetting;
import org.hexgridapi.utility.Vector2Int;

/**
 * Coordinate used to shape chunk as Square.
 * 
 * @author roah
 */
public final class SquareCoordinate extends ChunkCoordinate {

    private Vector2Int chunkPos;
    
    public SquareCoordinate() {
        chunkPos = new Vector2Int();
    }

    public SquareCoordinate(int x, int y) {
        chunkPos = new Vector2Int(x, y);
    }

    public SquareCoordinate(Vector2Int position) {
        chunkPos = position;
    }
    
    public SquareCoordinate(HexCoordinate hexCoord) {
        Vector2Int pos = hexCoord.toOffset();
        int x = ((int) FastMath.abs(pos.x) + (pos.x < 0 ? -1 : 0)) / chunkSize;
        int y = ((int) FastMath.abs(pos.y) + (pos.y < 0 ? -1 : 0)) / chunkSize;
        this.chunkPos = new Vector2Int(pos.x < 0 ? (x+1) * -1 : x, pos.y < 0 ? (y+1) * -1 : y);
    }

    @Override
    public ChunkCoordinate add(ChunkCoordinate coord) {
        if (getClass() != coord.getClass()) {
            throw new RuntimeException(coord.getClass() + " is not assignable to " + getClass());
        } else {
            final SquareCoordinate hexaCoord = (SquareCoordinate) coord;
            return add(hexaCoord.getValue());
        }
    }

    public ChunkCoordinate add(Vector2Int coord) {
        return add(coord.x, coord.y);
    }

    public ChunkCoordinate add(int x, int y) {
        return new SquareCoordinate(chunkPos.add(x, y));
    }

    @Override
    public HexCoordinate getChunkOrigin() {
        Vector2Int pos = new Vector2Int(
                chunkPos.x * chunkSize,
                chunkPos.y * chunkSize);
        return new HexCoordinate(HexCoordinate.Coordinate.OFFSET, pos);
    }

    @Override
    public HexCoordinate getChunkCenter() {
        Vector2Int pos = getChunkOrigin().toOffset().add(chunkSize/2);
        return new HexCoordinate(HexCoordinate.Coordinate.OFFSET, pos);
    }

    @Override
    public boolean containTile(HexCoordinate tile) {
        return isInside(tile.toOffset(), getChunkOrigin().toOffset());
    }

    private boolean isInside(Vector2Int tile, Vector2Int chunk) {
        return isInside(tile.x, chunk.x) && isInside(tile.y, chunk.y);
    }

    private boolean isInside(int tile, int chunk) {
        return FastMath.abs(tile) >= FastMath.abs(chunk)
                && FastMath.abs(tile) < FastMath.abs(chunk) + chunkSize;
    }

    private Vector2Int getValue() {
        return chunkPos;
    }

    @Override
    public String convertToString() {
        return chunkPos.toString();
    }

    @Override
    public ChunkCoordinate fromString(String str) throws NumberFormatException {
        return new SquareCoordinate(Vector2Int.fromString(str));
    }

    @Override
    public boolean equal(ChunkCoordinate obj) {
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SquareCoordinate other = (SquareCoordinate) obj;
        return !(this.chunkPos != other.chunkPos && (this.chunkPos == null || !this.chunkPos.equals(other.chunkPos)));
    }

    @Override
    public int hash() {
        int hash = 7;
        hash = 61 * hash + (this.chunkPos != null ? this.chunkPos.hashCode() : 0);
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChunkCoordinate> getBufferedChunk(ChunkCoordinate builderCoord, int bufferRadius) {
        ArrayList<ChunkCoordinate> list = new ArrayList<ChunkCoordinate>();
        for (int x = -bufferRadius; x <= bufferRadius; x++) {
            for (int y = -bufferRadius; y <= bufferRadius; y++) {
                list.add(((SquareCoordinate) builderCoord).add(x, y));
            }
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mesh genCollisionPlane(int bufferRadius) {
        float sizeX = chunkSize * HexSetting.HEX_WIDTH * (bufferRadius + 1);
        float sizeY = chunkSize * (HexSetting.HEX_RADIUS * 1.5f) - HexSetting.HEX_RADIUS / 2;
        float posY = 0;
        Vector3f[] vertices = new Vector3f[]{
            new Vector3f(-(sizeX), posY, -(HexSetting.HEX_RADIUS + sizeY * (bufferRadius + 1))), // top left

            new Vector3f(sizeX + chunkSize * HexSetting.HEX_WIDTH, posY,
            -(HexSetting.HEX_RADIUS + sizeY * (bufferRadius + 1))), // top right

            new Vector3f(-(sizeX), posY, sizeY * (bufferRadius + 2)), // bot left

            new Vector3f(sizeX + chunkSize * HexSetting.HEX_WIDTH, posY, sizeY * (bufferRadius + 2)) // bot right
        };
        Vector2f[] texCoord = new Vector2f[]{new Vector2f(), new Vector2f(1, 0), new Vector2f(0, 1), new Vector2f(1, 1)};
        int[] index = new int[]{0, 2, 1, 1, 2, 3};

        Mesh result = new Mesh();
        result.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        result.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        result.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(index));
        result.createCollisionData();
        result.updateBound();

        return result;
    }
}
