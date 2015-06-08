package org.hexgridapi.core.control.buffercontrol;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.util.ArrayList;
import java.util.List;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.geometry.builder.coordinate.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;
import org.hexgridapi.core.geometry.builder.ChunkCoordinate;
import org.hexgridapi.core.geometry.builder.coordinate.HexagonCoordinate;

/**
 *
 * @author roah
 */
public class HexagonBufferControl implements ChunkBuffer {

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
        float dimX = HexSetting.HEX_WIDTH * (HexSetting.CHUNK_SIZE + 1.5f);
        float dimY = HexSetting.HEX_RADIUS * 1.5f * (HexSetting.CHUNK_SIZE + 1f);

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
