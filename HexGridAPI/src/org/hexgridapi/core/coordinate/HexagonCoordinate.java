package org.hexgridapi.core.coordinate;

import org.hexgridapi.core.ChunkCoordinate;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.util.ArrayList;
import java.util.List;
import org.hexgridapi.core.geometry.HexSetting;
import org.hexgridapi.utility.Vector2Int;
import org.hexgridapi.utility.Vector3Int;

/**
 * Coordinate used to shape chunk as hexagon.
 * 
 * @author roah
 */
public class HexagonCoordinate extends ChunkCoordinate {

    private Vector3Int chunkPos;

    public HexagonCoordinate() {
        chunkPos = new Vector3Int();
    }

    public HexagonCoordinate(int x, int y, int z) {
        this.chunkPos = new Vector3Int(x, y, z);
    }

    public HexagonCoordinate(Vector3Int coord) {
        this.chunkPos = coord;
    }

    public HexagonCoordinate(HexCoordinate hexCoord) {
        Vector3Int cub = hexCoord.toCubic();
        chunkPos = new Vector3Int(
                cub.x + (cub.x < 0 ? -1 : 0) / chunkSize * 2,
                cub.y + (cub.y < 0 ? -1 : 0) / chunkSize * 2,
                cub.z + (cub.z < 0 ? -1 : 0) / chunkSize * 2);
    }

    @Override
    public ChunkCoordinate add(ChunkCoordinate coord) {
        if(getClass() != coord.getClass()){
            throw new RuntimeException(coord.getClass() + " is not assignable to "+ getClass());
        } else {
            final HexagonCoordinate hexaCoord = (HexagonCoordinate) coord;
            return add(hexaCoord.getValue());
        }
    }

    public ChunkCoordinate add(Vector3Int coord) {
        return new HexagonCoordinate(chunkPos.add(coord));
    }

    public ChunkCoordinate add(int x, int y, int z) {
        return new HexagonCoordinate(chunkPos.add(x, y, z));
    }

    public HexCoordinate getChunkOrigin() {
        Vector3Int pos = new Vector3Int(
                chunkPos.x + (chunkPos.x < 0 ? -1 : 0) * chunkSize * 2,
                chunkPos.y + (chunkPos.y < 0 ? -1 : 0) * chunkSize * 2,
                chunkPos.z + (chunkPos.z < 0 ? -1 : 0) * chunkSize * 2);
        return new HexCoordinate(pos);
    }

    @Override
    public HexCoordinate getChunkCenter() {
        return getChunkOrigin();
    }

    public boolean containTile(HexCoordinate tile) {
        return getChunkOrigin().hasInRange(tile, chunkSize);
    }

    private Vector3Int getValue() {
        return chunkPos;
    }

    @Override
    public ChunkCoordinate fromString(String str) throws NumberFormatException {
        return new HexagonCoordinate(Vector3Int.fromString(str));
    }

    @Override
    public String convertToString() {
        return chunkPos.toString();
    }

    @Override
    public int hash() {
        int hash = 3;
        hash = 71 * hash + (this.chunkPos != null ? this.chunkPos.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equal(ChunkCoordinate obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HexagonCoordinate other = (HexagonCoordinate) obj;
        if (this.chunkPos != other.chunkPos && (this.chunkPos == null || !this.chunkPos.equals(other.chunkPos))) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChunkCoordinate> getBufferedChunk(ChunkCoordinate bufferCoord, int bufferRadius) {
        ArrayList<ChunkCoordinate> list = new ArrayList<ChunkCoordinate>();
        HexCoordinate coord = new HexCoordinate(HexCoordinate.Coordinate.OFFSET, Vector2Int.ZERO);
        for (HexCoordinate c : coord.getCoordinateInRange(bufferRadius)) {
            list.add(((HexagonCoordinate) bufferCoord).add(c.toCubic().x, c.toCubic().y, c.toCubic().z));
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mesh genCollisionPlane(int bufferRadius) {
        float posY = 0;
        float dimX = HexSetting.HEX_WIDTH * (chunkSize + 1.5f);
        float dimY = HexSetting.HEX_RADIUS * 1.5f * (chunkSize + 1f);

        Vector3f[] vertices = new Vector3f[]{
            new Vector3f(-(dimX / 2), posY, -(dimY)), // top left

            new Vector3f(dimX / 2, posY, -(dimY)), // top right

            new Vector3f(-(dimX - HexSetting.HEX_RADIUS * 0.75f), posY, 0), // center left

            new Vector3f(dimX - HexSetting.HEX_RADIUS * 0.75f, posY, 0), // center right

            new Vector3f(-(dimX / 2), posY, dimY), // bot left

            new Vector3f(dimX / 2, posY, dimY) // bot right
        };
        Vector2f[] texCoord = new Vector2f[]{new Vector2f(), new Vector2f(0.5f, 0),
            new Vector2f(0, 0.5f), new Vector2f(0.5f, 0.5f), new Vector2f(0, 1f), new Vector2f(1f, 1f)};
        int[] index = new int[]{0, 2, 1, 1, 2, 3, 3, 2, 4, 4, 5, 3};

        Mesh result = new Mesh();
        result.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        result.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        result.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(index));
        result.createCollisionData();
        result.updateBound();

        return result;
    }
}
