package org.hexgridapi.core.control.chunkbuilder;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.hexgridapi.core.HexGrid;
import org.hexgridapi.core.control.ChunkControl;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.mesh.GreddyMesher;
import org.hexgridapi.utility.Vector2Int;

/**
 * Used by the ghost control to generate the Ghost Chunk.
 *
 * @author roah
 */
public abstract class GhostBuilder extends DefaultBuilder {

    public static GhostBuilder getBuilder(DefaultBuilder builder) {
        if (HexSetting.CHUNK_SHAPE_TYPE.equals(GreddyMesher.ShapeType.SQUARE)) {
            return new GhostSquareBuilder(builder);
        } else if (HexSetting.CHUNK_SHAPE_TYPE.equals(GreddyMesher.ShapeType.HEXAGON)) {
            return new GhostHexagonBuilder(builder);
        } else {
            throw new UnsupportedOperationException("There is no existing module for : " + HexSetting.CHUNK_SHAPE_TYPE);
        }
    }

    protected GhostBuilder(DefaultBuilder builder) {
        super(builder);
    }

    public MapData.GhostMode getMode() {
        return mode;
    }

    /**
     * The plane used to collide with the camera Raycast.
     * @return 
     */
    public Geometry getCollisionPlane() {
        Geometry collisionPlane = new Geometry("ghostCollision", genCollisionPlane());
        collisionPlane.setCullHint(Spatial.CullHint.Always);
//        collisionPlane.setMaterial(hexMaterial);
        return collisionPlane;
    }

    protected void genProcedural(int x, int y, Vector2Int chunkPosition, boolean onlyGround, Node container) {
        Node tileNode;
        if (container.getChild("TILES." + x + "|" + y) != null) {
            tileNode = (Node) container.getChild("TILES." + x + "|" + y);
            tileNode.detachAllChildren();
        } else {
            tileNode = new Node("TILES." + x + "|" + y);
            tileNode.setLocalTranslation(ChunkControl.getChunkWorldPosition(chunkPosition.add(x, y)));
        }
        getTiles(tileNode, onlyGround, chunkPosition.add(x, y));//greddyMesher.getMesh(onlyGround, chunkPosition.add(x, y)));
        if (container.getChild("TILES." + x + "|" + y) == null) {
            container.attachChild(tileNode);
        }
    }

    public final void generateGhost(HexGrid system, Node container, Vector2Int chunkPosition, boolean onlyGround) {
        if (mode.equals(MapData.GhostMode.GHOST_PROCEDURAL)) {
            generateGhostProceduralChunk(system, container, chunkPosition, onlyGround);
        } else if (mode.equals(MapData.GhostMode.PROCEDURAL)) {
            generateProceduralChunk(system, container, chunkPosition, onlyGround);
        } else if (mode.equals(MapData.GhostMode.GHOST)) {
            generateGhostChunk(system, container, chunkPosition, onlyGround);
        } else {
            throw new UnsupportedOperationException(mode + " is not a currently supported mode.");
        }
    }

    protected abstract void generateGhostProceduralChunk(HexGrid system, Node container, Vector2Int chunkPosition, boolean onlyGround);

    protected abstract void generateProceduralChunk(HexGrid system, Node container, Vector2Int chunkPosition, boolean onlyGround);

    protected abstract void generateGhostChunk(HexGrid system, Node container, Vector2Int chunkPosition, boolean onlyGround);

    protected abstract Mesh genCollisionPlane();
}
