package org.hexgridapi.core.geometry.builder;

import com.jme3.app.Application;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.Set;
import org.hexgridapi.core.control.buffercontrol.ChunkBufferControl;
import org.hexgridapi.core.geometry.builder.ghost.GhostControlCullingData;
import org.hexgridapi.core.data.MapData;

/**
 * Used by the ghost control to generate the Ghost Chunk.
 *
 * @author roah
 * @deprecated use ChunkBufferControl
 */
public abstract class GhostChunkBuilder extends ChunkBuilder {

    private ChunkBufferControl ghostControl;

    public GhostChunkBuilder(Application app, MapData mapData, ChunkBufferControl ghostControl) {
        super(app, mapData, true, false);
        this.ghostControl = ghostControl;
        Node node = new Node("GhostNode");
        builderNode.attachChild(node);
        node.addControl(ghostControl);
    }

    protected final void genProcedural(int x, int y, ChunkCoordinate chunkPosition, boolean onlyGround, Node container) {
        Node tileNode;
        if (container.getChild("TILES." + x + "|" + y) != null) {
            tileNode = (Node) container.getChild("TILES." + x + "|" + y);
            tileNode.detachAllChildren();
        } else {
            tileNode = new Node("TILES." + x + "|" + y);
            tileNode.setLocalTranslation(chunkPosition.add(x, y).getChunkOrigin().toWorldPosition());
        }
        addChunkTo(tileNode, chunkPosition.add(x, y));
        if (container.getChild("TILES." + x + "|" + y) == null) {
            container.attachChild(tileNode);
        }
    }

    public void generateGhostChunk(Set<ChunkCoordinate> hexMapChunklist, Node container, ChunkCoordinate chunkPosition, boolean onlyGround) {
        ArrayList<ChunkCoordinate> list = generateChunk();

        Geometry child = ((Geometry) ((Node) container.getChild("TILES.0|0")).getChild(0).clone(true));
        child.getMaterial().setColor("Color", new ColorRGBA(0, 0.2f, 1, 0.5f));

        for (ChunkCoordinate c : list) {
            //@todo
        }
    }

    public final void updateCulling(ChunkCoordinate chunkPosition, Node container) {
        ArrayList<GhostControlCullingData> dataList = updateCulling(chunkPosition);
        for (GhostControlCullingData data : dataList) {
            setCulling(container, data.getCoord(), data.isCull());
        }
    }

    private void setCulling(Node container, ChunkCoordinate coord, boolean set) {
        if (set) {
            if (mode.equals(MapData.GhostMode.GHOST)) {
                container.getChild("TILES." + coord.convertToString()).setCullHint(Spatial.CullHint.Always);
            } else {
                ((Node) container.getChild("TILES." + coord.convertToString())).detachAllChildren();
            }
        } else if (mode.equals(MapData.GhostMode.GHOST)) {
            container.getChild("TILES." + coord.convertToString()).setCullHint(Spatial.CullHint.Inherit);
        }
    }

    @Override
    public void addChunk(ChunkCoordinate chunkPos) {
        ghostControl.updateCulling();
        super.addChunk(chunkPos);
    }
    
    protected abstract ArrayList<GhostControlCullingData> updateCulling(ChunkCoordinate chunkPosition);

    protected abstract Mesh genCollisionPlane();

    public abstract ArrayList<ChunkCoordinate> generateChunk();
}
