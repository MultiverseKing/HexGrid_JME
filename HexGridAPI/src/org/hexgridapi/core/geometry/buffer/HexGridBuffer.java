package org.hexgridapi.core.geometry.buffer;

import com.jme3.app.Application;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.util.SkyFactory;
import com.jme3.water.SimpleWaterProcessor;
import java.util.ArrayList;
import org.hexgridapi.core.mousepicking.GridRayCastControl;
import org.hexgridapi.core.ChunkCoordinate;
import org.hexgridapi.core.HexGrid;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.hexgridapi.core.AbstractHexGridAppState;
import org.hexgridapi.core.geometry.ChunkBuilder;
import org.hexgridapi.events.BufferListener;
import org.hexgridapi.events.Register;

/**
 *
 * @author roah
 */
public class HexGridBuffer implements Register<BufferListener> {

    private final Node bufferNode = new Node(HexGridBuffer.class.getName() + ":Control:Node");
//    private final Node collisionNode = new Node(HexGridBuffer.class.getName() + ":Collision:Node");
    private final Node bufferFXNode = new Node(HexGridBuffer.class.getName() + ":FX:Node");
    private GridRayCastControl rayControl;
    private ArrayList<BufferListener> listeners = new ArrayList<BufferListener>();
    private Vector3f oldPosition;
    private ChunkCoordinate bufferPosition;
    private ChunkBuilder builder;
    private int bufferRadius = 1;
    private Geometry water;
    private BufferPositionProvider positionProvider;

    public HexGridBuffer(BufferPositionProvider positionProvider) {
        this.positionProvider = positionProvider;
        this.oldPosition = positionProvider.getBufferPosition().clone();
//        collisionNode.setCullHint(Spatial.CullHint.Always);
        bufferNode.attachChild(bufferFXNode);
    }

    public void initialize(Application app) {
        // Used to debug the positionProvider, 
        this.rayControl = new GridRayCastControl(app, null, ColorRGBA.Green); 
        HexGrid hexGrid = app.getStateManager().getState(AbstractHexGridAppState.class);
        builder = hexGrid.getBuilder();

//        hexGrid.getGridNode().attachChild(collisionNode);
        builder.getBuilderNode().attachChild(bufferNode);

        bufferFXNode.attachChild(SkyFactory.createSky(
                app.getAssetManager(), "org/hexgridapi/assets/Textures/BrightSky.dds", false));

        /**
         * @todo consume too much fps :'(
         * @todo the water have to be split from the sky.
         */
        // we create a water processor
        SimpleWaterProcessor waterProcessor = new SimpleWaterProcessor(app.getAssetManager());
//        waterProcessor.setReflectionScene(spatial.getParent());
        waterProcessor.setReflectionScene(hexGrid.getGridNode().getParent());// rootNode is used for reflection

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
        water = new Geometry("water", quad);
        water.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        water.setLocalTranslation(-100, 0.01f, 125);
//        water.setShadowMode(RenderQueue.ShadowMode.Receive);
        water.setMaterial(waterProcessor.getMaterial());
    }

    public void setPositionProvider(BufferPositionProvider positionProvider) {
        this.positionProvider = positionProvider;
    }
    
    public void setParam(int bufferRadius) {
        bufferPosition = ChunkCoordinate.getNewInstance();
        positionProvider.resetToOriginPosition(bufferPosition.getChunkCenter().toWorldPosition());
        this.bufferRadius = bufferRadius;
        initCollision();
        updateBufferPosition();

        if (!builder.buildVoidTile()) {
            bufferFXNode.attachChild(water);
        }
    }

    private void initCollision() {
//        collisionNode.detachAllChildren();
//        Geometry collisionPlane = new Geometry("ghostCollision",
//                ((BufferBuilder) bufferPosition).genCollisionPlane(bufferRadius));
//        collisionNode.attachChild(collisionPlane);
////        collisionPlane.setMaterial(app.getAssetManager().loadMaterial("org/hexgridapi/assets/Materials/hexMat.j3m"));
////        updateBufferPosition();
    }

    public void update(float tpf) {
        if (bufferPosition != null && !positionProvider.getBufferPosition().equals(oldPosition)) {
            ChunkCoordinate newCoord = ChunkCoordinate.getNewInstance(new HexCoordinate(positionProvider.getBufferPosition()));
            if (!hasInRange(newCoord)) {
                bufferPosition = newCoord;
                updateBufferPosition();
            }
            oldPosition = positionProvider.getBufferPosition().clone();
            rayControl.setDebugPosition(oldPosition);
        }
    }

    private boolean hasInRange(ChunkCoordinate testedCoord) {
        if (bufferPosition.getChunkOrigin().distanceTo(testedCoord.getChunkOrigin())
                >= (bufferRadius > 1 ? bufferRadius - 1 : bufferRadius) * ChunkCoordinate.getChunkSize()) {
            return false;
        }
        return true;
    }

    private void updateBufferPosition() {
        Vector3f pos = bufferPosition.getChunkOrigin().toWorldPosition();
//        collisionNode.setLocalTranslation(pos);
        bufferNode.setLocalTranslation(pos);
        updatePositionListeners();
    }

    public void showVoidTileFX(boolean hide) {
        if (hide && !bufferFXNode.hasChild(water)) {
            bufferFXNode.attachChild(water);
        } else {
            water.removeFromParent();
        }
    }

    public void register(BufferListener listener) {
        listeners.add(listener);
        if (bufferPosition != null) {
            listener.onPositionUpdate(bufferPosition);
        }
    }

    public void unregister(BufferListener listener) {
        listeners.remove(listener);
    }

    private void updatePositionListeners() {
        for (BufferListener l : listeners) {
            l.onPositionUpdate(bufferPosition);
        }
    }

    public int getRadius() {
        return bufferRadius;
    }
}