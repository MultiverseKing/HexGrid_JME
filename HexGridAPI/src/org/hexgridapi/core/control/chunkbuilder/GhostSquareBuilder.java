package org.hexgridapi.core.control.chunkbuilder;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.util.Set;
import org.hexgridapi.core.HexGrid;
import static org.hexgridapi.core.control.ChunkControl.getChunkWorldPosition;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.utility.Vector2Int;

/**
 * Used by the Ghost Control to generate Square chunk.
 * @todo optimisation avoid generating already generated chunk.
 * @author roah
 */
class GhostSquareBuilder extends GhostBuilder {

    public GhostSquareBuilder(DefaultBuilder module) {
        super(module);
    }

    @Override
    protected void generateGhostProceduralChunk(HexGrid system, Node container, Vector2Int chunkPosition, boolean onlyGround) {
        generate(system, container, chunkPosition, onlyGround);
    }

    @Override
    protected void generateProceduralChunk(HexGrid system, Node container, Vector2Int chunkPosition, boolean onlyGround) {
        generate(system, container, chunkPosition, onlyGround);
    }

    @Override
    public void generateGhostChunk(HexGrid system, Node container, Vector2Int chunkPosition, boolean onlyGround) {
        Geometry child = ((Geometry) ((Node)container.getChild("TILES.0|0")).getChild(0).clone(true));
        child.getMaterial().setColor("Color", new ColorRGBA(0, 0.2f, 1, 0.5f));
        for (int x = -HexSetting.GHOST_CONTROL_RADIUS; x <= HexSetting.GHOST_CONTROL_RADIUS; x++) {
            for (int y = -HexSetting.GHOST_CONTROL_RADIUS; y <= HexSetting.GHOST_CONTROL_RADIUS; y++) {
                if (!(x == 0 && y == 0)) {
                    Node tileNode = new Node("TILES." + x + "|" + y);
                    tileNode.attachChild(child.clone());
                    Vector3f pos = getChunkWorldPosition(chunkPosition.add(x, y));
                    tileNode.setLocalTranslation(pos);
                    container.attachChild(tileNode);
                    tileNode.setCullHint(Spatial.CullHint.Inherit);
                }
            }
        }
    }

    private void generate(HexGrid system, Node container, Vector2Int chunkPosition, boolean onlyGround) {
        Set<Vector2Int> list = system.getChunksNodes();
        for (int x = -HexSetting.GHOST_CONTROL_RADIUS; x <= HexSetting.GHOST_CONTROL_RADIUS; x++) {
            for (int y = -HexSetting.GHOST_CONTROL_RADIUS; y <= HexSetting.GHOST_CONTROL_RADIUS; y++) {
                Vector2Int pos = chunkPosition.add(x, y);
                if (!(x == 0 && y == 0)
                        && !list.contains(pos)) {
//                        if (generatedChunk.contains(pos)) {
//                            //@todo
//                        } else {
                    genProcedural(x, y, chunkPosition, onlyGround, container);
//                        }
                }
            }
        }
    }

    @Override
    protected Mesh genCollisionPlane() {
        float sizeX = HexSetting.CHUNK_SIZE * HexSetting.HEX_WIDTH * (HexSetting.GHOST_CONTROL_RADIUS + 1);
        float sizeY = HexSetting.CHUNK_SIZE * (HexSetting.HEX_RADIUS * 1.5f) - HexSetting.HEX_RADIUS / 2;
        float posY = 0;
        Vector3f[] vertices = new Vector3f[]{
            new Vector3f(-(sizeX), posY, -(HexSetting.HEX_RADIUS + sizeY * (HexSetting.GHOST_CONTROL_RADIUS + 1))), // top left

            new Vector3f(sizeX + HexSetting.CHUNK_SIZE * HexSetting.HEX_WIDTH, posY,
            -(HexSetting.HEX_RADIUS + sizeY * (HexSetting.GHOST_CONTROL_RADIUS + 1))), // top right

            new Vector3f(-(sizeX), posY, sizeY * (HexSetting.GHOST_CONTROL_RADIUS + 2)), // bot left

            new Vector3f(sizeX + HexSetting.CHUNK_SIZE * HexSetting.HEX_WIDTH, posY, sizeY * (HexSetting.GHOST_CONTROL_RADIUS + 2)) // bot right
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
