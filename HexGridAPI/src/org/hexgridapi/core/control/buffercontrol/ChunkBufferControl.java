package org.hexgridapi.core.control.buffercontrol;

import org.hexgridapi.events.BufferListener;
import com.jme3.app.Application;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.util.SkyFactory;
import com.jme3.water.SimpleWaterProcessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.hexgridapi.core.HexGrid;
import org.hexgridapi.core.control.ChunkControl;
import org.hexgridapi.core.control.GridRayCastControl;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.geometry.builder.ChunkCoordinate;
import org.hexgridapi.core.geometry.builder.ChunkBuilder;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.core.geometry.builder.coordinate.HexCoordinate;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.events.TileChangeListener;

/**
 * Chunk handling by himself and follow the camera.
 *
 * @todo when the buffer move only generate needed chunk and not all.
 * @todo shadow...
 * @todo if chunk position is > 1 from center remove shadow !?
 * @author roah
 */
public final class ChunkBufferControl extends ChunkControl {

    private final Node collisionNode = new Node("HexGridcollisionNode@" + getClass().getSimpleName());
    private final Node bufferFXNode = new Node("HexGridFXNode@" + getClass().getSimpleName());
    private final List<ChunkCoordinate> persistantBufferList;
    private Camera cam;
    private final int bufferRadius = 1;
    private GridRayCastControl rayControl;
    private Vector3f oldCamPosition = new Vector3f();
    private ArrayList<BufferListener> listeners = new ArrayList<BufferListener>();
    private boolean initialise;
    private final Application app;

    public ChunkBufferControl(Application app, MapData mapData, HexGrid system, ChunkBuilder builder) {
        super(builder, ChunkCoordinate.getNewInstance());
        this.app = app;
        this.cam = app.getCamera();
        system.getGridNode().attachChild(collisionNode);
        
        this.rayControl = new GridRayCastControl(app, system, collisionNode, ColorRGBA.Green);
        Node bufferNode = new Node("ControlNode@" + getClass().getSimpleName());
        builder.getBuilderNode().attachChild(bufferNode);
        persistantBufferList = getBufferedChunk(bufferRadius);
        
        mapData.registerTileChangeListener(tileChangeListener);
        bufferNode.addControl(this);
    }
    
    private final TileChangeListener tileChangeListener = new TileChangeListener() {
        public final void onTileChange(TileChangeEvent... events) {
            updateCulling();
        }

        @Override
        public void onGridReload() {
        }
    };

    private List<ChunkCoordinate> getBufferedChunk(int bufferRadius) {
        List<ChunkCoordinate> bufferList = ((ChunkBuffer) chunkPosition)
                .getBufferedChunk(ChunkCoordinate.getNewInstance(), bufferRadius);
        bufferList.remove(ChunkCoordinate.getNewInstance());
        return bufferList;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        //@todo
        if (builder.useProcedural()) {
            super.setSpatial(spatial);
        } else {
            super.setSpatial(spatial, false);
        }
        if (spatial != null) {
            Geometry collisionPlane = new Geometry("ghostCollision",
                    ((ChunkBuffer) chunkPosition).genCollisionPlane(bufferRadius));
            collisionNode.attachChild(collisionPlane);
//            collisionPlane.setMaterial(app.getAssetManager().loadMaterial("Materials/hexMat.j3m"));
            collisionPlane.setCullHint(Spatial.CullHint.Always);

            oldCamPosition = cam.getLocation().clone();
            if (!builder.useProcedural()) {
                generateBufferedChunk();
            } else if (!builder.showVoidTile()) {
                usewater();
            }
            updateBufferPosition();
            initialise = true;
        } else {
            // cleanup
        }
    }

    /**
     * @todo make it changeable
     */
    private void usewater() {
        bufferFXNode.attachChild(SkyFactory.createSky(
                app.getAssetManager(), "Textures/BrightSky.dds", false));

        // we create a water processor
        SimpleWaterProcessor waterProcessor = new SimpleWaterProcessor(app.getAssetManager());
        waterProcessor.setReflectionScene(spatial.getParent());

        // we set the water plane
        Vector3f waterLocation = new Vector3f(0, 0, 0);
        waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y, waterLocation.dot(Vector3f.UNIT_Y)));
        app.getViewPort().addProcessor(waterProcessor);

        // we set wave properties
        waterProcessor.setWaterDepth(-4);         // transparency of water
        waterProcessor.setDistortionScale(0.05f); // strength of waves
        waterProcessor.setWaveSpeed(0.01f);       // speed of waves
        waterProcessor.setWaterTransparency(0.2f);
        waterProcessor.setRenderSize(256, 256);

        // we define the wave size by setting the size of the texture coordinates
        Quad quad = new Quad(200, 200);
        quad.scaleTextureCoordinates(new Vector2f(6f, 6f));

        // we create the water geometry from the quad
        Geometry water = new Geometry("water", quad);
        water.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        water.setLocalTranslation(-100, 0, 125);
//        water.setShadowMode(RenderQueue.ShadowMode.Receive);
        water.setMaterial(waterProcessor.getMaterial());
        bufferFXNode.attachChild(water);
//        ((Node)spatial).attachChild(bufferFXNode);
        collisionNode.getParent().attachChild(bufferFXNode);
    }

    /**
     * Register listener to get the latest update from the buffer.
     *
     * @param listener to register.
     */
    public void registerListener(BufferListener listener) {
        listeners.add(listener);
    }

    /**
     * Un-register listener from the buffer.
     *
     * @param listener to remove.
     */
    public boolean removeListener(BufferListener listener) {
        return listeners.remove(listener);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (initialise && !spatial.getCullHint().equals(Spatial.CullHint.Always)
                && !cam.getLocation().equals(oldCamPosition)) {
            MouseInputEvent event = rayControl.castRay(null);
            if (event != null) {
                HexCoordinate pos = event.getPosition();
                if (pos != null){// && !chunkPosition.containTile(pos)) {
                    ChunkCoordinate newCoord = ChunkCoordinate.getNewInstance(pos);
                    if (!hasInRange(newCoord)) {
                        chunkPosition = newCoord;
                        updateBufferPosition();
                    }
                }
                oldCamPosition = cam.getLocation().clone();
            }
        }
    }

    private void updateBufferPosition() {
        Vector3f pos = chunkPosition.getChunkOrigin().toWorldPosition();
        collisionNode.setLocalTranslation(pos);
        bufferFXNode.setLocalTranslation(pos);
        spatial.setLocalTranslation(pos);
        if (builder.useProcedural()) {
            updateChunk();
            generateBufferedChunk();
        }
        updateListeners();
        updateCulling();
    }

    private void updateListeners() {
        for (BufferListener l : listeners) {
            l.positionUpdate(chunkPosition);
        }
    }
    
    /**
     * Update the culling to avoid overlapping chunk.
     * (HexGrid chunk and this chunk is handled separetely)
     */
    public void updateCulling() {
        Set<ChunkCoordinate> builderList = builder.getChunkList();
        persistantBufferList.add(ChunkCoordinate.getNewInstance());
        for (ChunkCoordinate c : persistantBufferList) {
            if (builderList.contains(c.add(chunkPosition))) {
                updateCulling(c, true);
            } else {
                updateCulling(c, false);
            }
        }
        persistantBufferList.remove(ChunkCoordinate.getNewInstance());
    }

    private void updateCulling(ChunkCoordinate coord, boolean isCull) {
        if (builder.useProcedural() && isCull) {
            try {
                ((Node)((Node) spatial).getChild("TILES." + coord)).detachAllChildren();
            } catch (NullPointerException ex){
                throw new NullPointerException("TILES." + coord + " is not found");
            }
        } else if (!builder.useProcedural() && isCull) {
            ((Node) spatial).getChild("TILES." + coord).setCullHint(Spatial.CullHint.Always);
        } else if (!builder.useProcedural() && !isCull) {
            ((Node) spatial).getChild("TILES." + coord).setCullHint(Spatial.CullHint.Inherit);
        }
    }

    /**
     * Generate all chunk arround the center.
     */
    private void generateBufferedChunk() {
        Geometry tmpVoidChunk = null;
        if (!builder.useProcedural()) {
            Node node = ((Node) ((Node) spatial).getChild("TILES." + ChunkCoordinate.getNewInstance()));
            builder.addVoidChunkTo(node);
            tmpVoidChunk = ((Geometry) node.getChild(0)).clone(true);
            tmpVoidChunk.getMaterial().setColor("Color", new ColorRGBA(0, 0.2f, 1f, 0.5f));
        }
        ChunkCoordinate coordZero = ChunkCoordinate.getNewInstance();
        for (ChunkCoordinate c : persistantBufferList) {
            if (!c.equal(coordZero)) {
                Node node = (Node) ((Node) spatial).getChild("TILES." + c);
                if (node == null) {
                    node = new Node("TILES." + c);
                    ((Node) spatial).attachChild(node);
                    node.setLocalTranslation(c.getChunkOrigin().toWorldPosition());
                } else if (builder.useProcedural()) {
                    node.detachAllChildren();
                }
                if (!builder.useProcedural()) {
                    node.attachChild(tmpVoidChunk.clone(false)); // Dereferencing possible null pointer...
                } else {
                    builder.addChunkTo(node, chunkPosition.add(c));
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideGhostTile(boolean hide) {
        for (ChunkCoordinate c : persistantBufferList) {
            Spatial geo = ((Node) ((Node) spatial).getChild("TILES." + c)).getChild("NO_TILE");
            if (geo != null && hide) {
                geo.setCullHint(Spatial.CullHint.Always);
            } else if (geo != null) {
                geo.setCullHint(Spatial.CullHint.Inherit);
            }
        }
    }

    private boolean hasInRange(ChunkCoordinate testedCoord) {
        List<ChunkCoordinate> bufferList = bufferRadius >= 1
                ? ((ChunkBuffer) chunkPosition).getBufferedChunk(ChunkCoordinate.getNewInstance(),
                bufferRadius - 1) : persistantBufferList;
        for (ChunkCoordinate c : bufferList) {
            if (testedCoord.equals(chunkPosition.add(c))) {
                return true;
            }
        }
        return false;
    }
}