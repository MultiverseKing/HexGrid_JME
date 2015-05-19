package org.hexgridapi.core;

import org.hexgridapi.core.data.MapData;
import com.jme3.app.Application;
import org.hexgridapi.core.control.ChunkControl;
import com.jme3.asset.AssetManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import org.hexgridapi.events.TileChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.hexgridapi.core.control.GhostControl;
import org.hexgridapi.core.data.procedural.ProceduralHexGrid;
import org.hexgridapi.core.control.chunkbuilder.DefaultBuilder;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.utility.Vector2Int;

/**
 *
 * @author roah
 */
public class HexGrid {

    /**
     * Node Containing all hex related data.
     */
    protected final Node gridNode = new Node("HexGridNode");
    /**
     * Node containing all Tiles.
     */
    protected final Node tileNode = new Node("HexTileNode");
    /**
     * Parameter used to generate the grid mesh.
     */
    private DefaultBuilder builder;
    protected MapData mapData;
    protected GhostControl ghostControl;
    protected HashMap chunksNodes = new HashMap<Vector2Int, Node>();
    protected Node areaRangeNode;
    protected ProceduralHexGrid mapGenerator;
    protected AssetManager assetManager;

    public HexGrid(MapData mapData, AssetManager assetManager, Node rootNode) {
        this.assetManager = assetManager;
        this.mapData = mapData;
        gridNode.attachChild(tileNode);
        rootNode.attachChild(gridNode);
        gridNode.setShadowMode(RenderQueue.ShadowMode.Off);
        tileNode.setShadowMode(RenderQueue.ShadowMode.Receive);
        mapData.registerTileChangeListener(tileChangeListener);
    }

    //@todo
    protected final void initialise(Application app) {
        this.builder = DefaultBuilder.getBuilder(app, mapData);
        if (!mapData.getMode().equals(MapData.GhostMode.NONE)) {
            Node node = new Node("GhostNode");
            ghostControl = new GhostControl(app, builder, mapData.getMode(), Vector2Int.ZERO, this);
            tileNode.attachChild(node);
            node.addControl(ghostControl);
        }
    }

    /**
     * @return the node containing all the API Node. (include tileNode)
     */
    public final Node getGridNode() {
        return gridNode;
    }

    /**
     * @return the node containing all tile.
     */
    public final Node getTileNode() {
        return tileNode;
    }

    /**
     * @return the currently used ghostControl.
     */
    public GhostControl getGhostControl() {
        return ghostControl;
    }

    public final Set<Vector2Int> getChunksNodes() {
        return Collections.unmodifiableSet(chunksNodes.keySet());
    }
    private final TileChangeListener tileChangeListener = new TileChangeListener() {
        public final void onTileChange(TileChangeEvent... events) {
            if (events.length > 1) {
                HashSet<Vector2Int> updatedChunk = new HashSet<Vector2Int>();
                for (int i = 0; i < events.length; i++) {
                    Vector2Int pos = events[i].getTilePos().getCorrespondingChunk();
                    if (updatedChunk.add(pos)) {
                        //updateChunk
                        updateChunk(events[i]);
//                        if (chunksNodes.containsKey(pos)) {
//                        } else {
//                            //add chunk
//                            addChunk(pos);
//                        }
                    }
                }
            } else {
                updateChunk(events[0]);
            }
        }
    };

    /**
     * Make change to tile according to the event.
     *
     * @param events contain information on the last tile event.
     */
    private void updateChunk(TileChangeEvent event) {
        Vector2Int chunkPos = event.getTilePos().getCorrespondingChunk();
        if (!chunksNodes.containsKey(chunkPos)) {
            addChunk(chunkPos);
            if (!mapData.getMode().equals(MapData.GhostMode.NONE)) {
                ghostControl.updateCulling();
            }
//        } else if (!(event.getNewTile() == null && event.getOldTile() == null)) {
//            updateChunk(chunkPos);
        } else {
//            System.err.println("old && new tile is null, an error have occurs, this will be ignored.");
//            Used when forcing a tile to be ignored even by the procedural generator
            updateChunk(chunkPos);
        }
    }

    protected final void updateChunk(Vector2Int chunkPos) {
        ((Node) chunksNodes.get(chunkPos)).getControl(ChunkControl.class).update();
//        if (((Node) chunksNodes.get(chunkPos)).getControl(ChunkControl.class).isEmpty()) {
        if (!mapData.contain(chunkPos)) {
            removeChunk(chunkPos);
        } else {
            updatedChunk(((Node) chunksNodes.get(chunkPos)).getControl(ChunkControl.class));
        }
    }

    protected final void addChunk(Vector2Int chunkPos) {
        Node chunk = new Node(chunkPos.toString());
        chunksNodes.put(chunkPos, chunk);
        chunk.addControl(new ChunkControl(builder, chunkPos, false));
        tileNode.attachChild(chunk);
        insertedChunk(chunk.getControl(ChunkControl.class));
    }

    protected final void removeChunk(Vector2Int chunkPos) {
        ((Node) chunksNodes.get(chunkPos)).removeFromParent();
        chunksNodes.remove(chunkPos);
        removedChunk(chunkPos);
    }

    protected void insertedChunk(ChunkControl control) {
    }

    protected void updatedChunk(ChunkControl control) {
    }

    protected void removedChunk(Vector2Int pos) {
    }

    public void cleanup() {
        mapData.removeTileChangeListener(tileChangeListener);
    }
}
