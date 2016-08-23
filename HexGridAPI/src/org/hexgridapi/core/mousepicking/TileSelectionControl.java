package org.hexgridapi.core.mousepicking;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import org.hexgridapi.core.AbstractHexGridAppState;
import org.hexgridapi.core.HexGrid;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.hexgridapi.core.data.HexTile;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.geometry.mesh.MeshGenerator;
import org.hexgridapi.events.MapDataListener;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.events.Registerable;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.events.TileInputListener;
import org.hexgridapi.events.TileSelectionListener;

/**
 * Handle the default selection cursor. <br>
 * Also store the latest selected tile position and height. <br>
 * Used by {@link GridMouseControlAppState} by default.
 *
 * @author roah
 */
public class TileSelectionControl implements Registerable<TileSelectionListener>, TileInputListener {
    
    private static Material mat;
    private final Node selectionRootNode = new Node(TileSelectionControl.class.getSimpleName() + ":Node");
    private final Node selectionListNode = new Node(TileSelectionControl.class.getSimpleName() + ":selectionGroup:Node");
    private final Mesh mesh = MeshGenerator.getSingleMesh(0);
    private final ArrayList<TileSelectionListener> listeners = new ArrayList<>();
    private ArrayList<HexCoordinate> selectionList = new ArrayList<>();
    private HexCoordinate selectedTile = new HexCoordinate();
    private CursorControl cursorControl;
    private boolean isSelectionGroup = false;
    private Application app;

    public TileSelectionControl() {
        selectionRootNode.attachChild(selectionListNode);
    }
    
    public void initialise(Application app) {
        this.app = app;
        if (mat == null) {
            mat = app.getAssetManager().loadMaterial("org/hexgridapi/assets/Materials/hexMat.j3m");
        }
        /**
         * Activate the input to interact with the grid.
         */
        app.getInputManager().addMapping("selectionGrp", new KeyTrigger(KeyInput.KEY_LSHIFT), new KeyTrigger(KeyInput.KEY_RSHIFT));
        app.getInputManager().addListener(keyListener, new String[]{"selectionGrp"});
        app.getInputManager().addListener(mouseListener, MouseInputEvent.MouseInputEventType.RMB.toString());
        /**
         * Register listener.
         */
        HexGrid hexGrid = app.getStateManager().getState(AbstractHexGridAppState.class);
        hexGrid.getMapData().register(dataListener);
        if(cursorControl == null) {
            cursorControl = new CursorControl(app, selectionRootNode);
        }
        hexGrid.getGridNode().attachChild(selectionRootNode);
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Listeners">
    @Override
    public void onMouseAction(MouseInputEvent event) {
        if (event.getPosition() != null) {
            setSelected(event.getPosition(), event.getHeight());
        }
    }
    
    private final ActionListener keyListener = (String name, boolean isPressed, float tpf) -> {
        if (name.equals("selectionGrp") && isPressed) {
            isSelectionGroup = true;
        } else if (name.equals("selectionGrp") && !isPressed) {
            isSelectionGroup = false;
        }
    };
    
    private final ActionListener mouseListener = (String name, boolean isPressed, float tpf) -> {
        if (name.equals(MouseInputEvent.MouseInputEventType.RMB.toString()) && !isPressed) {
            clearSelectionGroup();
        }
    };
    
    //@todo require a look, it change the mesh of a tile by a newly generated one
    private final MapDataListener dataListener = (TileChangeEvent... events) -> {
        for (TileChangeEvent event : events) {
            if (selectionList.contains(event.getTilePos())) {
                Geometry tile = (Geometry) selectionRootNode.getChild(event.getTilePos().toOffset().toString());
                if (event.getNewTile() != null) {
//                        ((Geometry) tile).setMesh(getMesh(events[i].getNewTile().getHeight()));
                    setPosition(tile, event.getTilePos().toWorldPosition(event.getNewTile().getHeight()));
                    cursorControl.setHeight(event.getNewTile().getHeight());
                    //                    tile.setLocalTranslation(event.getTilePos().toWorldPosition());
                    //                    coord.put(event.getTilePos(), event.getNewTile().getHeight());
                } else {
                    //                    coord.put(event.getTilePos(), 0);
//                        ((Geometry) tile).setMesh(getMesh(0));
                    setPosition(tile, event.getTilePos().toWorldPosition());
                    cursorControl.setHeight(0);
                    //                    tile.setLocalTranslation(event.getTilePos().toWorldPosition());
                }
            } else if (selectedTile.equals(event.getTilePos()) && event.getNewTile() != null) {
                cursorControl.setHeight(event.getNewTile().getHeight());
            }
        }
    };
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Registers">
    @Override
    public void register(TileSelectionListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void unregister(TileSelectionListener listener) {
        listeners.remove(listener);
    }

    private void updateListeners() {
        listeners.stream().forEach((listener) -> {
            listener.onTileSelectionUpdate(selectedTile, selectionList);
        });
    }
    // </editor-fold>
    
    /**
     * Force the cursor position to a specifiate position.
     * 
     * @param pos where to put the cursor on the grid
     * @return false if the position does not exist in mapData
     */
    public boolean setSelected(HexCoordinate pos) {
        HexTile t = app.getStateManager().getState(AbstractHexGridAppState.class)
                .getMapData().getTile(pos);
        if(t != null){
            setSelected(pos, t.getHeight());
            return true;
        } else {
            return false;
        }
    }
    
    private void setSelected(HexCoordinate pos, int height) {
        addToGroup(pos, height);
        selectedTile = pos;
        cursorControl.setPosition(pos, height);
        updateListeners();
    }

    private void addToGroup(HexCoordinate pos, int height) {
        if (isSelectionGroup) {
            if (!selectionList.contains(pos)) {
                addGeo(pos, height);
                selectionList.add(pos);
            } else {
                selectionListNode.getChild(pos.toOffset().toString()).removeFromParent();
                selectionList.remove(pos);
            }
        }
    }
    
    private void addGeo(HexCoordinate pos, int height) {
        Geometry geo = new Geometry(pos.toOffset().toString(), mesh);
        geo.setMaterial(mat);
        setPosition(geo, pos.toWorldPosition(height));
        selectionListNode.attachChild(geo);
    }

    private void setPosition(Geometry geo, Vector3f worldPos) {
        worldPos.y += 0.01f;
        geo.setLocalTranslation(worldPos);
    }

//    private Mesh getMesh(int height) {
//        if (!singleTile.containsKey(height)) {
//            singleTile.put(height, MeshGenerator.getSingleMesh(height));
//        }
//        return singleTile.get(height);
//    }
    
    private void clearSelectionGroup() {
        if(!selectionList.isEmpty()) {
            selectionList.clear();
            selectionListNode.getChildren().stream().forEach((s) -> {
                s.removeFromParent();
            });
        }
    }

    public HexCoordinate getSelectedPos() {
        return selectedTile;
    }

    public ArrayList<HexCoordinate> getSelectedList() {
        return selectionList;
    }

    void cleanup() {
        app.getInputManager().deleteMapping("selectionGrp");
        app.getInputManager().removeListener(keyListener);
        app.getInputManager().removeListener(mouseListener);
        /**
         * Unregister listener.
         */
        AppState state =  app.getStateManager().getState(GridMouseControlAppState.class);
        if(state != null) {
            app.getStateManager().getState(GridMouseControlAppState.class).unregister(this);
        }
        HexGrid hexGrid = app.getStateManager().getState(AbstractHexGridAppState.class);
        if(hexGrid != null) {
            hexGrid.getMapData().unregister(dataListener);
            hexGrid.getGridNode().detachChild(selectionRootNode);
        }
    }
}
