package org.hexgridapi.core.geometry;

import org.hexgridapi.core.geometry.buffer.HexGridBuffer;
import org.hexgridapi.core.geometry.buffer.BufferedChunk;
import org.hexgridapi.core.AbstractHexGridAppState;
import org.hexgridapi.core.ChunkCoordinate;
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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.MapParam;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.hexgridapi.core.geometry.mesh.GreddyMesher;
import org.hexgridapi.events.BufferListener;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.events.MapDataListener;
import org.hexgridapi.utility.Vector2Int;
import org.slf4j.LoggerFactory;

/**
 * Used to generate a chunk Geometry,
 * this class define the Shader/Material to use.
 *
 * @todo texture array
 * @author roah
 */
public class ChunkBuilder {

    private final Node builderNode = new Node(ChunkBuilder.class.getName() + ":Node");
    private final HashMap<ChunkCoordinate, ChunkControl> chunkNodes = new HashMap<ChunkCoordinate, ChunkControl>();
    private BufferedChunk bufferedChunk;
    private GreddyMesher greddyMesher;
    private Material hexMaterial;
    private AssetManager assetManager;
    protected String texturePath;
    protected boolean buildVoidTile;
    protected boolean showVoidTile;
    protected boolean onlyGround;
    private int bufferRadius;
    private Vector2Int mapSize;

    public ChunkBuilder(String texturePath) {
        this.texturePath = texturePath;
    }

    public void initialize(Application app, HexGridBuffer gridBuffer) {
        this.assetManager = app.getAssetManager();
        MapData mapData = app.getStateManager().getState(AbstractHexGridAppState.class).getMapData();
        GreddyMesher mesher;
        Material mat;
        if (greddyMesher == null) {
            if (true) {//!app.getRenderer().getCaps().contains(Caps.TextureArray)) {
                LoggerFactory.getLogger(ChunkBuilder.class).warn("The hardware does not support TextureArray");
                mesher = new GreddyMesher(mapData, false);
                mat = assetManager.loadMaterial("org/hexgridapi/assets/Materials/hexMat.j3m");
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
            builderNode.setShadowMode(RenderQueue.ShadowMode.Receive);
        }
        gridBuffer.register(bufferListener);
        mapData.register(dataListener);
    }

    public void setParam(MapParam param, MapData mapData) {
        cleanupChunks();
        this.onlyGround = param.isOnlyGround();
        this.buildVoidTile = param.isBuildVoidTile();
        this.showVoidTile = buildVoidTile;
        this.bufferRadius = param.getBufferRadius();
        this.mapSize = param.getMapSize();
        if (mapSize.equals(Vector2Int.ZERO)) {
            if (bufferedChunk == null) {
                bufferedChunk = new BufferedChunk(this);
            }
            bufferedChunk.setParam(param.isUsingProceduralGen(), param.getBufferRadius());
        } else {
            if (bufferedChunk != null) {
                bufferedChunk.getSpatial().removeControl(bufferedChunk);
            }
            buildMapOfSize();
        }
    }

    private void buildMapOfSize() {
        for (int x = 0; x < mapSize.x; x++) {
            for (int y = 0; y < mapSize.y; y++) {
                addChunk(ChunkCoordinate.getNewInstance(
                        new HexCoordinate(HexCoordinate.Coordinate.OFFSET,
                        new Vector2Int(x, y).multiply(ChunkCoordinate.getChunkSize()))));
            }
        }
    }

    /**
     * Generate a chunk and attach it to the specifiate Node.
     *
     * @param parent node to attach the geometry.
     * @param chunkPosition on the map.
     */
    public void addChunkTo(Node parent, ChunkCoordinate chunkPosition, ChunkControl control) {
        HashMap<String, Mesh> mesh = greddyMesher.getMesh(onlyGround, buildVoidTile, chunkPosition);
        if (mesh.size() == 1 && !buildVoidTile && mesh.containsKey(MapData.DefaultTextureValue.NO_TILE.toString())) {
            removeChunk(chunkPosition);
            LoggerFactory.getLogger(ChunkBuilder.class).debug(" remove chunk {}", chunkPosition);
        } else {
            for (String value : mesh.keySet()) {
                parent.attachChild(getGeometry(value, mesh.get(value)));
            }
        }

        if (buildVoidTile && !showVoidTile) {
            control.hideVoidTile(true);
        }
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
            if (value != null && value.equals("NO_TILE") && buildVoidTile) {
                TextureKey k = new TextureKey("org/hexgridapi/assets/Textures/HexField/EMPTY_TEXTURE_KEY.png", false);
                k.setGenerateMips(true);
                text = assetManager.loadTexture(k);
                mat.setColor("Color", ColorRGBA.Blue);
            } else {
                TextureKey k = new TextureKey((value.equals("EMPTY_TEXTURE_KEY") ? "org/hexgridapi/assets/Textures/HexField/"
                        : texturePath) + value + ".png", false);
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
    private final MapDataListener dataListener = new MapDataListener() {
        public final void onTileChange(TileChangeEvent... events) {
            if (events.length > 1) {
                // Used to avoid having the same chunk updated multiple time
                HashSet<ChunkCoordinate> updatedChunk = new HashSet<ChunkCoordinate>();
                for (int i = 0; i < events.length; i++) {
                    ChunkCoordinate pos = ChunkCoordinate.getNewInstance(events[i].getTilePos());
                    if (updatedChunk.add(pos)) {
                        updateChunk(pos);
                    }
                }
            } else { // Same than the one above but optimised for only one event
                updateChunk(ChunkCoordinate.getNewInstance(events[0].getTilePos()));
            }
            if (mapSize.equals(Vector2Int.ZERO)) {
                bufferedChunk.onTileChange(events);
            }
        }
    };
    private BufferListener bufferListener = new BufferListener() {
        @Override
        public void onPositionUpdate(ChunkCoordinate newBufferPosition) {
            if (mapSize.equals(Vector2Int.ZERO)) {
                LoggerFactory.getLogger(ChunkBuilder.class).debug("Update Builder culling.");
                Iterator<ChunkCoordinate> it = chunkNodes.keySet().iterator();
                while (it.hasNext()) {
                    ChunkCoordinate next = it.next();
                    if (next.getChunkOrigin().distanceTo(newBufferPosition.getChunkOrigin())
                            >= (bufferRadius + 1) * ChunkCoordinate.getChunkSize()) {
                        chunkNodes.get(next).hide();
                    } else {
                        chunkNodes.get(next).show();
                    }
                }
                bufferedChunk.onPositionUpdate(newBufferPosition);
            }
        }
    };

    /**
     * Update the defined chunk.
     *
     * @todo remove the chunk witout updating it.
     */
    private void updateChunk(ChunkCoordinate chunkPos) {
        if (!chunkNodes.containsKey(chunkPos)) {
            addChunk(chunkPos);
        } else {
            chunkNodes.get(chunkPos).updateChunk();
            if (chunkNodes.get(chunkPos).isEmpty()) {
                removeChunk(chunkPos);
            }
        }
        if (bufferedChunk != null) {
            bufferedChunk.updateCulling();
        }
    }

    protected void addChunk(ChunkCoordinate chunkPos) {
        Node chunk = new Node(chunkPos.toString());
        ChunkControl control = new ChunkControl(this, chunkPos);
        chunk.addControl(control);
        builderNode.attachChild(chunk);
        chunkNodes.put(chunkPos, control);
    }

    protected final void removeChunk(ChunkCoordinate chunkPos) {
        chunkNodes.get(chunkPos).getSpatial().removeFromParent();
        chunkNodes.remove(chunkPos);
    }

    public final void hideVoidTile(boolean setVisible) {
        this.showVoidTile = !setVisible;
        for (ChunkControl chunk : chunkNodes.values()) {
            chunk.hideVoidTile(setVisible);
        }
    }

    public final boolean isEmpty() {
        return chunkNodes.isEmpty();
    }

    public final Set<ChunkCoordinate> getChunkList() {
        return Collections.unmodifiableSet(chunkNodes.keySet());
    }

    public boolean showVoidTile() {
        return showVoidTile;
    }

    public boolean buildVoidTile() {
        return buildVoidTile;
    }

    public Node getBuilderNode() {
        return builderNode;
    }

    public void cleanup() {
//        mapData.removeTileChangeListener(tileChangeListener);
    }

    private void cleanupChunks() {
        Iterator<Map.Entry<ChunkCoordinate, ChunkControl>> it = chunkNodes.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getValue().getSpatial().removeFromParent();
            it.remove();
        }
    }
}
