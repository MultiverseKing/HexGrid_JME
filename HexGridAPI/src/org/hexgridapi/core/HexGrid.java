package org.hexgridapi.core;

import com.jme3.app.Application;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import org.hexgridapi.core.geometry.buffer.BufferPositionProvider;
import org.hexgridapi.core.geometry.buffer.HexGridBuffer;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.geometry.ChunkBuilder;

/**
 * Handle the connection between "tile data" and "tile visual".
 *
 * @author roah
 */
public class HexGrid {

    private Application app;
    /**
     * Node Containing all Api visual data.
     */
    protected final Node hexGridAPINode = new Node(HexGrid.class.getName() + ":Node");
    /**
     * Node Containing all Api visual data.
     */
    protected final MapData mapData;
    /**
     * Parameter used to generate the grid mesh.
     */
    protected final ChunkBuilder builder;
    /**
     * Control used to define the position of the camera and FX.
     */
    private final HexGridBuffer gridBuffer;

    /**
     * Create a new Instance of the HexGrid System.
     * /!\ {@link #getGridNode()} have to be attach to the rootNode
     *
     */
    public HexGrid(MapData mapData, BufferPositionProvider positionProvider) {
        this(mapData, positionProvider, "Textures/HexField/");
    }

    /**
     * Create a new Instance of the HexGrid System.
     * /!\ {@link #getGridNode()} have to be attach to the rootNode
     *
     * @param texturePath path to use to load texture (default =
     * "Textures/HexField/")
     */
    public HexGrid(MapData mapData, BufferPositionProvider positionProvider,String texturePath) {
        this.mapData = mapData;
        this.builder = new ChunkBuilder(texturePath);
        hexGridAPINode.attachChild(builder.getBuilderNode());
        hexGridAPINode.setShadowMode(RenderQueue.ShadowMode.Off);
        gridBuffer = new HexGridBuffer(positionProvider);
    }

    public final void initialise(Application app) {
        this.app = app;
        builder.initialize(app, gridBuffer);
        gridBuffer.initialize(app);
    }
    
    public void update(float tpf){
        gridBuffer.update(tpf);
    }

    public void setParam(MapParam param) {
        mapData.setGenerator(param.isUsingProceduralGen(), param.getProceduralGenerator());
        builder.setParam(param, mapData);
        gridBuffer.setParam(param.getBufferRadius());
    }
    
    public void setBufferPositionProvider(BufferPositionProvider provider){
        gridBuffer.setPositionProvider(provider);
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
    public final ChunkBuilder getBuilder() {
        return builder;
    }

    public MapData getMapData() {
        return mapData;
    }

    /**
     * Hide/Show all tile who contain a null pointer.
     *
     * @param setVisible
     */
    public void hideVoidTile(boolean setVisible) {
        builder.hideVoidTile(setVisible);
        gridBuffer.showVoidTileFX(setVisible);
    }

    public void cleanup() {
        mapData.Cleanup();
        builder.cleanup();
    }
}