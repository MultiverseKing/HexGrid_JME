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
import org.hexgridapi.core.geometry.mesh.MeshGenerator;
import org.hexgridapi.events.MapDataListener;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.events.Register;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.events.TileInputListener;
import org.hexgridapi.events.TileSelectionListener;

/**
 * Generate tile on the field to be used as selection control. <br>
 * Used by {@link GridMouseControlAppState} by default.
 *
 * @author roah
 */
public class TileSelectionControl implements Register<TileSelectionListener> {

//    private static HashMap<Integer, Mesh> singleTile = new HashMap<Integer, Mesh>();
    private static Material mat;
    private final Node selectionRootNode = new Node(TileSelectionControl.class.getSimpleName() + ":Node");
    private final Node groupNode = new Node(TileSelectionControl.class.getSimpleName() + ":selectionGroup:Node");
    private final Mesh mesh = MeshGenerator.getSingleMesh(0);
    private ArrayList<HexCoordinate> coords = new ArrayList<HexCoordinate>();
    private ArrayList<TileSelectionListener> listeners = new ArrayList<TileSelectionListener>();
    private HexCoordinate selectedTile = new HexCoordinate();
    private CursorControl cursorControl;
    private boolean isSelectionGroup = false;
    private Application app;

    public TileSelectionControl() {
        selectionRootNode.attachChild(groupNode);
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
//        app.getInputManager().addListener(mouseListener, new String[]{"Cancel"});
        /**
         * Register listener.
         */
        app.getStateManager().getState(GridMouseControlAppState.class).register(tileInputListener);
        HexGrid hexGrid = app.getStateManager().getState(AbstractHexGridAppState.class);
        hexGrid.getMapData().register(dataListener);
        if(cursorControl == null) {
            cursorControl = new CursorControl(app, selectionRootNode);
        }
        hexGrid.getGridNode().attachChild(selectionRootNode);
    }
    //-----------
    // <editor-fold defaultstate="collapsed" desc=" Listeners">
    private TileInputListener tileInputListener = new TileInputListener() {
        @Override
        public void onMouseAction(MouseInputEvent event) {
            if (event.getType().equals(MouseInputEvent.MouseInputEventType.LMB) && event.getPosition() != null) {
//                HexTile tile = mapData.getTile(event.getEventPosition());
                addTile(event.getPosition(), event.getHeight());
//                editorMainGUI.showCurrentSelectionCount(tileSelectionControl.getTileCount());
            } else if (event.getPosition() != null) {
                setSelected(event.getPosition(), event.getHeight());
//                cursorControl.setPosition(event.getEventPosition(), system.getTileHeight(event.getEventPosition()));
            }
        }
    };
    private final ActionListener keyListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("selectionGrp") && isPressed) {
                isSelectionGroup = true;
            } else if (name.equals("selectionGrp") && !isPressed) {
                isSelectionGroup = false;
            }
        }
    };
    private final ActionListener mouseListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals(MouseInputEvent.MouseInputEventType.RMB.toString()) && !isPressed) {
                clearSelectionGroup();
            }
        }
    };
    //@todo require a look, it change the mesh of a tile by a new generated on
    private final MapDataListener dataListener = new MapDataListener() {
        @Override
        public void onTileChange(TileChangeEvent... events) {
            for (int i = 0; i < events.length; i++) {
                if (coords.contains(events[i].getTilePos())) {
                    Geometry tile = (Geometry) selectionRootNode.getChild(events[i].getTilePos().toOffset().toString());
                    if (events[i].getNewTile() != null) {
//                        ((Geometry) tile).setMesh(getMesh(events[i].getNewTile().getHeight()));
                        setPosition(tile, events[i].getTilePos().toWorldPosition(events[i].getNewTile().getHeight()));
                        cursorControl.setHeight(events[i].getNewTile().getHeight());
                        //                    tile.setLocalTranslation(event.getTilePos().toWorldPosition());
                        //                    coord.put(event.getTilePos(), event.getNewTile().getHeight());
                    } else {
                        //                    coord.put(event.getTilePos(), 0);
//                        ((Geometry) tile).setMesh(getMesh(0));
                        setPosition(tile, events[i].getTilePos().toWorldPosition());
                        cursorControl.setHeight(0);
                        //                    tile.setLocalTranslation(event.getTilePos().toWorldPosition());
                    }
                } else if (selectedTile.equals(events[i].getTilePos()) && events[i].getNewTile() != null) {
                    cursorControl.setHeight(events[i].getNewTile().getHeight());
                }
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
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onTileSelectionUpdate(selectedTile, coords);
        }
    }
    // </editor-fold>

    private void addTile(HexCoordinate pos, int height) {
        if (isSelectionGroup) {
            if (!coords.contains(pos)) {
                addGeo(pos, height);
                coords.add(pos);
            } else {
                groupNode.getChild(pos.toOffset().toString()).removeFromParent();
                coords.remove(pos);
            }
        }
        setSelected(pos, height);
    }

    private void setSelected(HexCoordinate pos, int height) {
        selectedTile = pos;
        cursorControl.setPosition(pos, height);
        updateListeners();
    }

    // @todo require opti
    private void addGeo(HexCoordinate pos, int height) {
        Geometry geo = new Geometry(pos.toOffset().toString(), mesh);
        geo.setMaterial(mat);
        setPosition(geo, pos.toWorldPosition(height));
        groupNode.attachChild(geo);
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
        coords.clear();
        for (Spatial s : groupNode.getChildren()) {
            s.removeFromParent();
        }
    }

    public HexCoordinate getSelectedPos() {
        return selectedTile;
    }

    public ArrayList<HexCoordinate> getSelectedList() {
        return coords;
    }

    TileInputListener getInputListener() {
        return tileInputListener;
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
            app.getStateManager().getState(GridMouseControlAppState.class).unregister(tileInputListener);
        }
        HexGrid hexGrid = app.getStateManager().getState(AbstractHexGridAppState.class);
        if(hexGrid != null) {
            hexGrid.getMapData().unregister(dataListener);
            hexGrid.getGridNode().detachChild(selectionRootNode);
        }
    }
}
