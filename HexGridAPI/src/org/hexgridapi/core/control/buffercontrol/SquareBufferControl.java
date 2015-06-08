package org.hexgridapi.core.control.buffercontrol;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.util.ArrayList;
import java.util.List;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.geometry.builder.ChunkCoordinate;
import org.hexgridapi.core.geometry.builder.coordinate.SquareCoordinate;

/**
 *
 * @author roah
 */
public class SquareBufferControl implements ChunkBuffer {

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
        float sizeX = HexSetting.CHUNK_SIZE * HexSetting.HEX_WIDTH * (bufferRadius + 1);
        float sizeY = HexSetting.CHUNK_SIZE * (HexSetting.HEX_RADIUS * 1.5f) - HexSetting.HEX_RADIUS / 2;
        float posY = 0;
        Vector3f[] vertices = new Vector3f[]{
            new Vector3f(-(sizeX), posY, -(HexSetting.HEX_RADIUS + sizeY * (bufferRadius + 1))), // top left

            new Vector3f(sizeX + HexSetting.CHUNK_SIZE * HexSetting.HEX_WIDTH, posY,
            -(HexSetting.HEX_RADIUS + sizeY * (bufferRadius + 1))), // top right

            new Vector3f(-(sizeX), posY, sizeY * (bufferRadius + 2)), // bot left

            new Vector3f(sizeX + HexSetting.CHUNK_SIZE * HexSetting.HEX_WIDTH, posY, sizeY * (bufferRadius + 2)) // bot right
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
