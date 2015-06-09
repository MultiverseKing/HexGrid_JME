package org.hexgridapi.core.geometry.builder;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hexgridapi.core.HexGrid;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.control.ChunkControl;
import org.hexgridapi.core.control.buffercontrol.ChunkBuffer;
import org.hexgridapi.core.control.buffercontrol.ChunkBufferControl;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.geometry.mesh.GreddyMesher;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.events.TileChangeListener;

/**
 * Used to generate a chunk Geometry,
 * this class define the Shader/Material to use.
 *
 * @todo texture array
 * @author roah
 */
public class ChunkBuilder {

    private Node builderNode;
    private MapData mapData;
    private Material hexMaterial;
    private GreddyMesher greddyMesher;
    private AssetManager assetManager;
    private boolean useBuffer;
    protected HashMap<ChunkCoordinate, ChunkControl> chunksNodes = new HashMap<ChunkCoordinate, ChunkControl>();
    protected boolean showVoidTile;
    protected boolean onlyGround;

    /**
     * Return the proper builderNode to use for generating chunk.
     *
     * @param app
     * @param mapData
     * @param showVoidTile
     * @param onlyGround
     */
    public ChunkBuilder(MapData mapData) {
        this.useBuffer = mapData.getGridParameters().isUseBuffer();
        this.showVoidTile = mapData.getGridParameters().isUseVoidTile();
        this.onlyGround = mapData.getGridParameters().isOnlyGround();
        this.mapData = mapData;
        this.builderNode = new Node("BuilderNode");
        builderNode.setShadowMode(RenderQueue.ShadowMode.Receive);

        mapData.registerTileChangeListener(tileChangeListener);
    }

    public void initialise(Application app, HexGrid system) {
        this.assetManager = app.getAssetManager();
        GreddyMesher mesher;
        Material mat;
        if (true) {//!app.getRenderer().getCaps().contains(Caps.TextureArray)) {
            Logger.getLogger(ChunkBuilder.class.getName()).log(Level.WARNING,
                    "The hardware does not support TextureArray");
            mesher = new GreddyMesher(mapData, false);
            mat = assetManager.loadMaterial("Materials/hexMat.j3m");
            mat.setName("hexMaterial");
        } else {
//            mesher = new GreddyMesher(mapData, true);
//            mat = new Material(assetManager, "MatDefs/UnshadedArray.j3md");
//            mat.setName("arrayTextureMaterial");
//            List<Image> images = new ArrayList<Image>();
//            for (int i = 0; i < mapData.getTextureKeys().size(); i++) {
//                images.add(assetManager.loadTexture(HexSetting.TEXTURE_PATH
//                        + mapData.getTextureKeys().get(i) + ".png").getImage());
//            }
//            TextureArray arrayTexture = new TextureArray(images);
//            mat.setTexture("ColorMap", arrayTexture);
//            mat.getAdditionalRenderState().setDepthTest(true);
////            mat.getAdditionalRenderState().setColorWrite(true);
//            mat.getAdditionalRenderState().setDepthWrite(true);
        }
        hexMaterial = mat;
        greddyMesher = mesher;
        
        
        if (useBuffer && ChunkCoordinate.getBuilderCoordinateType().getClass().isInstance(ChunkBuffer.class)) {
            ChunkBufferControl bufferControl = new ChunkBufferControl(app, mapData, system, this);
        } else if (useBuffer) {
            throw new UnsupportedOperationException(ChunkCoordinate.getBuilderCoordinateType().getSimpleName()
                    + " does not Implements BufferControl.");
        }
    }
    
    /**
     * Generate a chunk and attach it to the specifiate Node.
     *
     * @param parent node to attach the geometry.
     * @param chunkPosition on the map.
     */
    public void addChunkTo(Node parent, ChunkCoordinate chunkPosition) {
        HashMap<String, Mesh> mesh = greddyMesher.getMesh(onlyGround, showVoidTile, chunkPosition);
        for (String value : mesh.keySet()) {
            parent.attachChild(getGeometry(value, mesh.get(value)));
        }
        
        
//        if (!hexMaterial.getName().equals("arrayTextureMaterial")) {
//            for (String value : mesh.keySet()) {
//                Material mat = hexMaterial.clone();
//                Geometry tile = new Geometry(value != null ? value : "debug", mesh.get(value));
//                Texture text;
//                /**
//                 * Debuging purpose
//                 * if (value == null && (mode.equals(MapData.GhostMode.GHOST)
//                 * || mode.equals(MapData.GhostMode.GHOST_PROCEDURAL))) {
//                 * TextureKey k = new TextureKey(HexSetting.TEXTURE_PATH +
//                 * "EMPTY_TEXTURE_KEY.png", false);
//                 * k.setGenerateMips(true);
//                 * text = assetManager.loadTexture(k);
//                 * mat.setColor("Color", ColorRGBA.Red);
//                 * } else
//                 */
//                if (value != null && value.equals("NO_TILE") && showVoidTile) {
//                    TextureKey k = new TextureKey(HexSetting.TEXTURE_PATH + "EMPTY_TEXTURE_KEY.png", false);
//                    k.setGenerateMips(true);
//                    text = assetManager.loadTexture(k);
//                    mat.setColor("Color", ColorRGBA.Blue);
//                } else {
//                    TextureKey k = new TextureKey(HexSetting.TEXTURE_PATH + value + ".png", false);
//                    k.setGenerateMips(true);
//                    text = assetManager.loadTexture(k);
//                }
//                text.setWrap(Texture.WrapMode.Repeat);
//
//                mat.setTexture("ColorMap", text);
////            mat.setTexture("DiffuseMap", text);
////            mat.getAdditionalRenderState().setWireframe(true);
////            tile.getMesh().setMode(Mesh.Mode.Points);
//                tile.setMaterial(mat);
////                if (HexSetting.CHUNK_SHAPE_TYPE.equals(GreddyMesher.ShapeType.HEXAGON)) { // @todo
////                    tile.setLocalTranslation(new Vector3f(
////                            -(HexSetting.HEX_WIDTH * HexSetting.CHUNK_SIZE
////                            - ((HexSetting.CHUNK_SIZE & 1) == 0 ? 0 : -HexSetting.HEX_WIDTH / 2)), 0,
////                            -(HexSetting.HEX_RADIUS * 1.5f * HexSetting.CHUNK_SIZE)));
////                }
//                parent.attachChild(tile);
//            }
//        } else {
//            Geometry tile = new Geometry("Geometry.ArrayTexture.TILES.0|0", mesh.get("ArrayTextureMesh")); //@todo
//            tile.setMaterial(hexMaterial);
//            tile.setShadowMode(RenderQueue.ShadowMode.Inherit);
//            parent.attachChild(tile);
//        }
    }

    public void addVoidChunkTo(Node parent) {
        Mesh mesh = greddyMesher.getMesh(onlyGround, true);
        parent.attachChild(getGeometry("NO_TILE", mesh));
    }

    private Geometry getGeometry(String value, Mesh mesh) {
        if (!greddyMesher.useArrayTexture()) {
            Material mat = hexMaterial.clone();
            Geometry tile = new Geometry(value != null ? value : "debug", mesh);
            Texture text;
            /**
             * Debuging purpose
             * if (value == null && (mode.equals(MapData.GhostMode.GHOST)
             * || mode.equals(MapData.GhostMode.GHOST_PROCEDURAL))) {
             * TextureKey k = new TextureKey(HexSetting.TEXTURE_PATH +
             * "EMPTY_TEXTURE_KEY.png", false);
             * k.setGenerateMips(true);
             * text = assetManager.loadTexture(k);
             * mat.setColor("Color", ColorRGBA.Red);
             * } else
             */
            if (value != null && value.equals("NO_TILE") && showVoidTile) {
                TextureKey k = new TextureKey(HexSetting.TEXTURE_PATH + "EMPTY_TEXTURE_KEY.png", false);
                k.setGenerateMips(true);
                text = assetManager.loadTexture(k);
                mat.setColor("Color", ColorRGBA.Blue);
            } else {
                TextureKey k = new TextureKey(HexSetting.TEXTURE_PATH + value + ".png", false);
                k.setGenerateMips(true);
                text = assetManager.loadTexture(k);
            }
            text.setWrap(Texture.WrapMode.Repeat);

            mat.setTexture("ColorMap", text);
//            mat.setTexture("DiffuseMap", text);
//            mat.getAdditionalRenderState().setWireframe(true);
//            tile.getMesh().setMode(Mesh.Mode.Points);
            tile.setMaterial(mat);
//                if (HexSetting.CHUNK_SHAPE_TYPE.equals(GreddyMesher.ShapeType.HEXAGON)) { // @todo
//                    tile.setLocalTranslation(new Vector3f(
//                            -(HexSetting.HEX_WIDTH * HexSetting.CHUNK_SIZE
//                            - ((HexSetting.CHUNK_SIZE & 1) == 0 ? 0 : -HexSetting.HEX_WIDTH / 2)), 0,
//                            -(HexSetting.HEX_RADIUS * 1.5f * HexSetting.CHUNK_SIZE)));
//                }
            return tile;
        } else {
            Geometry tile = new Geometry("Geometry.ArrayTexture.TILES.0|0", mesh); //@todo
            tile.setMaterial(hexMaterial);
            tile.setShadowMode(RenderQueue.ShadowMode.Inherit);
            return tile;
        }

    }

    public Node getBuilderNode() {
        return builderNode;
    }

    protected void addChunk(ChunkCoordinate chunkPos) {
        Node chunk = new Node(chunkPos.toString());
        ChunkControl control = new ChunkControl(this, chunkPos);
        chunk.addControl(control);
        builderNode.attachChild(chunk);
        chunksNodes.put(chunkPos, control);
    }

    /**
     * Update the defined chunk.
     */
    private void updateChunk(ChunkCoordinate chunkPos) {
        if (!chunksNodes.containsKey(chunkPos)) {
            addChunk(chunkPos);
//        } else if (!(event.getNewTile() == null && event.getOldTile() == null)) {
//            updateChunk(chunkPos);
        } else {
//            System.err.println("old && new tile is null, an error have occurs, this will be ignored.");
//            Used when forcing a tile to be ignored even by the procedural generator
            if (!mapData.contain(chunkPos)) {
                removeChunk(chunkPos);
            } else {
                chunksNodes.get(chunkPos).updateChunk();
            }
        }
    }

    protected final void removeChunk(ChunkCoordinate chunkPos) {
        chunksNodes.get(chunkPos).getSpatial().removeFromParent();
        chunksNodes.remove(chunkPos);
    }
    private final TileChangeListener tileChangeListener = new TileChangeListener() {
        public final void onTileChange(TileChangeEvent... events) {
            if (events.length > 1) {
                HashSet<ChunkCoordinate> updatedChunk = new HashSet<ChunkCoordinate>(); // Used to avoid having the same chunk updated multiple time
                for (int i = 0; i < events.length; i++) {
                    ChunkCoordinate pos = ChunkCoordinate.getNewInstance(events[i].getTilePos());
                    if (updatedChunk.add(pos)) {
                        //updateChunk
                        updateChunk(pos);
//                        if (chunksNodes.containsKey(pos)) {
//                        } else {
//                            //add chunk
//                            addChunk(pos);
//                        }
                    }
                }
            } else { // Same than the one above but optimised for only one event
                updateChunk(ChunkCoordinate.getNewInstance(events[0].getTilePos()));
            }
        }

        @Override
        public void onGridReload() {
        }
    };

    public final void hideGhostTile(boolean setVisible) {
        for (ChunkControl chunk : chunksNodes.values()) {
            chunk.hideGhostTile(setVisible);
        }
    }

    public final boolean isEmpty() {
        return chunksNodes.isEmpty();
    }

    public final Set<ChunkCoordinate> getChunkList() {
        return Collections.unmodifiableSet(chunksNodes.keySet());
    }

    public final boolean useProcedural() {
        return mapData.getGenerator() == null ? false : true;
    }

    public final void cleanup() {
        mapData.removeTileChangeListener(tileChangeListener);
    }

    public boolean useBuffer() {
        return useBuffer;
    }

    public boolean showVoidTile() {
        return showVoidTile;
    }
}
