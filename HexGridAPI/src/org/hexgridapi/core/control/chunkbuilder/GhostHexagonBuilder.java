package org.hexgridapi.core.control.chunkbuilder;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import org.hexgridapi.core.HexGrid;
import org.hexgridapi.core.HexSetting;
import static org.hexgridapi.core.control.ChunkControl.getChunkWorldPosition;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;

/**
 * Used by the Ghost control to generate Hexagonal Chunk.
 * @deprecated too complex...
 *
 * @author roah
 */
class GhostHexagonBuilder extends GhostBuilder {

    public GhostHexagonBuilder(DefaultBuilder module) {
        super(module);
    }

    @Override
    protected void generateGhostProceduralChunk(HexGrid system, Node container, Vector2Int chunkPosition, boolean onlyGround) {
        generate(system, container, chunkPosition, onlyGround, true);
    }

    @Override
    protected void generateProceduralChunk(HexGrid system, Node container, Vector2Int chunkPosition, boolean onlyGround) {
        generate(system, container, chunkPosition, onlyGround, true);
    }

    @Override
    public void generateGhostChunk(HexGrid system, Node container, Vector2Int chunkPosition, boolean onlyGround) {
        Geometry child = ((Geometry) ((Node)container.getChild("TILES.0|0")).getChild(0).clone(true));
        child.getMaterial().setColor("Color", new ColorRGBA(0, 1, 1, 1f));

        HexCoordinate coord = new HexCoordinate();
        for (HexCoordinate c : coord.getCoordinateInRange(HexSetting.GHOST_CONTROL_RADIUS)) {
            Node tileNode = new Node("TILES." + c.toOffset().x + "|" + c.toOffset().y);
            container.attachChild(tileNode);
            tileNode.attachChild(child.clone());
//            tileNode.setLocalTranslation(getChunkWorldPosition(chunkPosition.add(c.toOffset().x, c.toOffset().y)));
            tileNode.setLocalTranslation(getChunkWorldPosition(chunkPosition.add(c.toOffset())));
            tileNode.setCullHint(Spatial.CullHint.Inherit);
        }
    }
    
    public void generate(HexGrid system, Node container, Vector2Int chunkPosition, boolean onlyGround, boolean procedural) {
        HexCoordinate coord = new HexCoordinate();
        for (HexCoordinate c : coord.getCoordinateInRange(HexSetting.GHOST_CONTROL_RADIUS)) {
            genProcedural(c.toOffset().x, c.toOffset().y, chunkPosition, onlyGround, container);
        }
    }

    @Override
    protected Mesh genCollisionPlane() {
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
