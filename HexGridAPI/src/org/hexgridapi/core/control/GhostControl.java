package org.hexgridapi.core.control;

import org.hexgridapi.events.GhostListener;
import com.jme3.app.Application;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hexgridapi.core.HexGrid;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.control.chunkbuilder.DefaultBuilder;
import org.hexgridapi.core.control.chunkbuilder.GhostBuilder;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.mesh.GreddyMesher;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;

/**
 * Chunk handling by himself and follow the camera.
 *
 * @todo shadow...
 * @todo if chunk position is > 1 from center remove shadow !?
 * @author roah
 */
public class GhostControl extends ChunkControl {

    private final Node collisionNode = new Node("GhostCollision");
    private final MapData.GhostMode mode;
    private final HexGrid system;
    private final Camera cam;
    private GridRayCastControl rayControl;
    private Vector3f oldCamPosition = new Vector3f();
    private ArrayList<GhostListener> listeners = new ArrayList<GhostListener>();

    public GhostControl(Application app, DefaultBuilder builder,
            MapData.GhostMode mode, Vector2Int chunkPosition, HexGrid system) {
        super(GhostBuilder.getBuilder(builder), chunkPosition, false);
        if (mode.equals(MapData.GhostMode.NONE)) {
            throw new UnsupportedOperationException(mode + " isn't allowed for Ghost Control");
        }
        this.cam = app.getCamera();
        collisionNode.attachChild(((GhostBuilder)super.builder).getCollisionPlane());
        this.rayControl = new GridRayCastControl(app, collisionNode, ColorRGBA.Green);
        this.system = system;
        this.mode = mode;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial != null && spatial instanceof Node) {
            ((Node) spatial).getParent().getParent().attachChild(collisionNode);
            oldCamPosition = cam.getLocation().clone();
            if (((GhostBuilder) builder).getMode().equals(MapData.GhostMode.GHOST)) {
                ((GhostBuilder) builder).generateGhost(system, (Node) spatial, chunkPosition, onlyGround);
            }
            updatePosition(true);
        } else if (spatial == null) {
            // cleanup
        } else {
            throw new UnsupportedOperationException("Provided spatial must be a Node.");
        }
    }

    public void registerListener(GhostListener listener) {
        listeners.add(listener);
    }

    public boolean removeListener(GhostListener listener) {
        return listeners.remove(listener);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (spatial != null && !spatial.getCullHint().equals(Spatial.CullHint.Always)
                && !cam.getLocation().equals(oldCamPosition)) {
            MouseInputEvent event = rayControl.castRay(null);
            if (event != null) {
                HexCoordinate pos = event.getPosition();
                if (pos != null && !pos.getCorrespondingChunk().equals(chunkPosition)
                        && !isInRange(pos.getCorrespondingChunk())) {
                    this.chunkPosition = pos.getCorrespondingChunk();
                    updatePosition(false);
                }
                oldCamPosition = cam.getLocation().clone();
            }
        }
    }

    private void updatePosition(boolean initialise) {
        Vector3f pos = getChunkWorldPosition(chunkPosition);
        collisionNode.setLocalTranslation(pos);
        spatial.setLocalTranslation(pos);
        if (mode.equals(MapData.GhostMode.GHOST_PROCEDURAL)
                || mode.equals(MapData.GhostMode.PROCEDURAL)) {
            if (!initialise) {
                update();
                updateListeners();
            }
            ((GhostBuilder) builder).generateGhost(system, (Node) spatial, chunkPosition, onlyGround);
        }
        updateCulling();
    }

    private void updateListeners() {
        for (GhostListener l : listeners) {
            l.positionUpdate(chunkPosition);
        }
    }

    /**
     * Internal use.
     * Update the culling to avoid overlapping chunk.
     * (HexGrid chunk and this chunk is handled separetely)
     */
    public void updateCulling() {
        Set<Vector2Int> list = system.getChunksNodes();
        if (HexSetting.CHUNK_SHAPE_TYPE.equals(GreddyMesher.ShapeType.SQUARE)) {
            for (int x = -HexSetting.GHOST_CONTROL_RADIUS; x <= HexSetting.GHOST_CONTROL_RADIUS; x++) {
                for (int y = -HexSetting.GHOST_CONTROL_RADIUS; y <= HexSetting.GHOST_CONTROL_RADIUS; y++) {
                    setCulling(x, y, list.contains(chunkPosition.add(x, y)));
                }
            }
        } else {
            HexCoordinate coord = new HexCoordinate(HexCoordinate.Coordinate.OFFSET, Vector2Int.ZERO);
            for (HexCoordinate c : coord.getCoordinateInRange(HexSetting.GHOST_CONTROL_RADIUS)) {
                Vector2Int offset = c.toOffset();
                setCulling(offset.x, offset.y, list.contains(chunkPosition.add(offset.x, offset.y)));
            }
        }
    }

    private void setCulling(int x, int y, boolean set) {
        if (set) {
            if (mode.equals(MapData.GhostMode.GHOST)) {
                ((Node) spatial).getChild("TILES." + x + "|" + y).setCullHint(Spatial.CullHint.Always);
            } else {
                ((Node) ((Node) spatial).getChild("TILES." + x + "|" + y)).detachAllChildren();
            }
        } else if (mode.equals(MapData.GhostMode.GHOST)) {
            ((Node) spatial).getChild("TILES." + x + "|" + y).setCullHint(Spatial.CullHint.Inherit);
        }
    }

    private boolean isInRange(Vector2Int pos) {
        if (HexSetting.CHUNK_SHAPE_TYPE.equals(GreddyMesher.ShapeType.SQUARE)) {
            for (int x = -(HexSetting.GHOST_CONTROL_RADIUS - 1); x <= (HexSetting.GHOST_CONTROL_RADIUS - 1); x++) {
                for (int y = -(HexSetting.GHOST_CONTROL_RADIUS - 1); y <= (HexSetting.GHOST_CONTROL_RADIUS - 1); y++) {
                    if (pos.equals(chunkPosition.add(x, y))) {
                        return true;
                    }
                }
            }
        } else {
            HexCoordinate coord = new HexCoordinate(HexCoordinate.Coordinate.OFFSET, Vector2Int.ZERO);
            for (HexCoordinate c : coord.getCoordinateInRange(HexSetting.GHOST_CONTROL_RADIUS)) {
                Vector2Int offset = c.toOffset();
                if (pos.equals(chunkPosition.add(offset.x, offset.y))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Culling set to Inherit.
     */
    public void show() {
        spatial.setCullHint(Spatial.CullHint.Inherit);
    }

    /**
     * Culling set to Always.
     */
    public void hide() {
        spatial.setCullHint(Spatial.CullHint.Always);
    }

    /**
     * Hide all Ghost Tile
     * (GhostTile are tile who didn't have any data linked to them)
     * aka: if (tile == null) tile = ghostTile.
     * /!\ does not work when using GhostMode.Procedural
     */
    public void hideGhostTile(boolean hide) {
        if (mode.equals(MapData.GhostMode.GHOST_PROCEDURAL)) {
            for (int x = -HexSetting.GHOST_CONTROL_RADIUS; x <= HexSetting.GHOST_CONTROL_RADIUS; x++) {
                for (int y = -HexSetting.GHOST_CONTROL_RADIUS; y <= HexSetting.GHOST_CONTROL_RADIUS; y++) {
                    Spatial geo = ((Node) ((Node) spatial).getChild("TILES." + x + "|" + y)).getChild("NO_TILE");
                    if (geo != null && hide) {
                        geo.setCullHint(Spatial.CullHint.Always);
                    } else if (geo != null) {
                        geo.setCullHint(Spatial.CullHint.Inherit);
                    }
                }
            }
        } else {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                    "{0} does not allow the hiding.", mode);
        }
    }
}