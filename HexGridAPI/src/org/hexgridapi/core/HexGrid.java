package org.hexgridapi.core;

import com.jme3.app.Application;
import org.hexgridapi.core.data.MapData;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import org.hexgridapi.core.geometry.builder.ChunkBuilder;

/**
 * Handle the connection between "tile data" and "tile visual".
 *
 * @author roah
 */
public class HexGrid {

    /**
     * Node Containing all Api visual data.
     */
    protected final Node hexGridAPINode = new Node("HexGridAPINode");
    /**
     * Parameter used to generate the grid mesh.
     */
    protected ChunkBuilder builder;
//    protected GhostControl ghostControl;
    /**
     * @deprecated
     */
    protected Node areaRangeNode;

    /**
     * Create a new Instance of the HexGrid System.
     * /!\ {@link #getGridNode()} have to be attach to the rootNode
     *
     * @param mapData
     */
    public HexGrid(MapData mapData) {
        this.builder = new ChunkBuilder(mapData);
        hexGridAPINode.attachChild(builder.getBuilderNode());
        hexGridAPINode.setShadowMode(RenderQueue.ShadowMode.Off);
    }
    
    public final void initialise(Application app) {
        builder.initialise(app, this);
    }

    /**
     * @return the node containing all the API Node. (include tileNode)
     */
    public final Node getGridNode() {
        return hexGridAPINode;
    }

    /**
     * @return the node containing all tile.
     */
    public final Node getBuilderNode() {
        return builder.getBuilderNode();
    }

    public void cleanup() {
        builder.cleanup();
    }
}
