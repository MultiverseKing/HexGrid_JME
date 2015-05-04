package org.hexgridapi.core.appstate;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hexgridapi.core.HexTile;
import org.hexgridapi.core.MapData;
import org.hexgridapi.core.control.GridRayCastControl;
import org.hexgridapi.core.control.TileSelectionControl;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.events.MouseInputEvent.MouseInputEventType;
import org.hexgridapi.events.TileInputListener;
import org.hexgridapi.events.MouseRayListener;

/**
 * Take care of all mouse input happening on the grid.
 *
 * @author Eike Foede, roah
 */
public class MouseControlSystem extends AbstractAppState {

    private GridRayCastControl rayCastControl;
    private Application app;
    private ArrayList<TileInputListener> inputListeners = new ArrayList<TileInputListener>();
    private ArrayList<MouseRayListener> rayListeners = new ArrayList<MouseRayListener>(3);
    private TileSelectionControl tileSelectionControl = new TileSelectionControl();
    private int listenerPulseIndex = -1;
    private Vector2f lastScreenMousePos = new Vector2f(0, 0);
    private MapData mapData;
    private boolean isLock = false;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.app = app;
        mapData = stateManager.getState(MapDataAppState.class).getMapData();
        /**
         * Activate the input to interact with the grid.
         */
        app.getInputManager().addListener(tileActionListener, new String[]{"Confirm", "Cancel"});
        /**
         * Activate the RaycastDebug.
         */
        rayCastControl = new GridRayCastControl(app, app.getStateManager().getState(AbstractHexGridAppState.class).getTileNode(), ColorRGBA.Red);
        tileSelectionControl.initialise(app);
    }

    /**
     * Register a listener to respond to Tile Input.
     *
     * @param listener to register.
     */
    public void registerTileInputListener(TileInputListener listener) {
        inputListeners.add(listener);
    }

    /**
     * Remove a listener to respond to Tile Input.
     *
     * @param listener to register.
     */
    public void removeTileInputListener(TileInputListener listener) {
        inputListeners.remove(listener);
    }

    /**
     * Add a listener for the mouse Raycasting.
     *
     * @param listener
     */
    public void registerRayInputListener(MouseRayListener listener) {
        rayListeners.add(listener);
        inputListeners.add(listener);
    }

    /**
     * Remove a listener from the mouse Raycasting.
     *
     * @param listener
     */
    public void removeRayInputListener(MouseRayListener listener) {
        rayListeners.remove(listener);
        inputListeners.remove(listener);
    }

    /**
     * Diseable the input used to interact with the grid.
     */
    public void removeInput() {
        app.getInputManager().removeListener(tileActionListener);
        clearDebug();
    }
    private final ActionListener tileActionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if (listenerPulseIndex == -1) {
                if (name.equals("Confirm") && !isPressed) {
                    castRay(MouseInputEventType.LMB);
                } else if (name.equals("Cancel") && !isPressed) {
                    castRay(MouseInputEventType.RMB);
                }
            } else {
                inputListeners.get(listenerPulseIndex).onMouseAction(
                        new MouseInputEvent(MouseInputEventType.PULSE, tileSelectionControl.getSelectedPos(), 
                        mapData.getTile(tileSelectionControl.getSelectedPos()).getHeight(),
                        rayCastControl.get3DRay(GridRayCastControl.CastFrom.MOUSE), null));
            }
        }
    };

    public void clearDebug() {
        rayCastControl.clearRayDebug();
    }
    
    @Override
    public void update(float tpf) {
        if (listenerPulseIndex != -1) {
            Vector2f newMousePos = app.getInputManager().getCursorPosition().normalize();
            if (!newMousePos.equals(lastScreenMousePos)) {
                castRay(MouseInputEventType.PULSE);
                lastScreenMousePos = newMousePos;
            }
        }
    }

    /**
     * Activate the cursor on pulse mode, Raycast will follow the mouse, Have to
     * be called by the the same listener to disable. The pulse mode lock other
     * update.
     *
     * @todo Ray listener support
     * @param listener calling for it.
     * @return false if an error happen or if already on pulseMode.
     */
    public boolean setCursorPulseMode(TileInputListener listener) {
        if (listenerPulseIndex == -1) {
            //We keep track of the listener locking the input.
            if (!inputListeners.contains(listener)) {
                inputListeners.add(listener);
            }
            listenerPulseIndex = inputListeners.indexOf(listener);
            if (initialized) {
                lastScreenMousePos = app.getInputManager().getCursorPosition();
                return true;
            } else {
                return true;
            }
        } else {
            /**
             * We check if the listener calling the pulseMode is the same than
             * the one who activated it. if it is the same we desable the pulse
             * mode.
             */
            if (inputListeners.contains(listener) && inputListeners.indexOf(listener) == listenerPulseIndex) {
                listenerPulseIndex = -1;
                return true;
            } else if (inputListeners.contains(listener) && inputListeners.indexOf(listener) != listenerPulseIndex) {
                Logger.getGlobal().log(Level.WARNING, "{0} : Pulse already locked by : {1} , Lock requested by : {2}",
                        new Object[]{getClass().getName(), inputListeners.get(listenerPulseIndex).getClass().toString(), listener.toString()});
                return false;
            } else {
                Logger.getGlobal().log(Level.WARNING, "{0} : Listener not registered :  {1}.", new Object[]{getClass().getName(), listener.toString()});
                return false;
            }
        }
    }

    private void castRay(MouseInputEventType mouseInput) {
        Ray ray = rayCastControl.get3DRay(GridRayCastControl.CastFrom.MOUSE);
        MouseInputEvent event;
        if (!mouseInput.equals(MouseInputEventType.PULSE)) {
                event = callRayActionListeners(mouseInput, ray);
            if (event == null) {
                event = rayCastControl.castRay(ray);
                if (event != null && event.getPosition() != null) { // && !event.getEventPosition().equals(lastHexPos)) {
                    HexTile tile = mapData.getTile(event.getPosition());
                    callMouseInputActionListeners(new MouseInputEvent(event, mouseInput,
                            tile != null ? tile.getHeight() : 0));
                }
            } else {// if (!event.getEventPosition().equals(lastHexPos)) {
                callMouseInputActionListeners(event);
            }
        } else {
            tileSelectionControl.onMouseAction(rayCastControl.castRay(ray));
        }
    }

    /**
     * @param mouseInput L or R listener to call
     * @param event event to pass
     */
    private void callMouseInputActionListeners(MouseInputEvent event) {
        for (TileInputListener l : inputListeners) {
            l.onMouseAction(event);
        }
    }

    /**
     * @todo When multiple ray listeners run on same time, the closest got the
     * event.
     * @param mouseInputType
     * @param ray
     */
    private MouseInputEvent callRayActionListeners(MouseInputEventType mouseInputType, Ray ray) {
        MouseInputEvent event = null;
        for (MouseRayListener l : rayListeners) {
            event = l.MouseRayInputAction(mouseInputType, ray);
            if (event != null) {
                return event;
            }
        }
        return event;
    }
    
    public TileSelectionControl getSelectionControl() {
        return tileSelectionControl;
    }
//    private void moveCursor(HexCoordinate tilePos) {
////        if(enable <= 0){
//        if (!isLock) {
//            initCursor();
//            Vector3f pos = tilePos.convertToWorldPosition();
//            HexTile tile = mapData.getTile(tilePos);
//            //        cursor.setLocalTranslation(pos.x, (tile != null ? tile.getHeight() * HexSetting.FLOOR_OFFSET : HexSetting.GROUND_HEIGHT * HexSetting.FLOOR_OFFSET)
//            //                + ((tilePos.getAsOffset().y & 1) == 0 ? 0.01f : 0.02f), pos.z + cursorOffset);
//            cursor.setLocalTranslation(pos.x, (tile != null ? tile.getHeight() * HexSetting.FLOOR_OFFSET : 0)
//                    + ((tilePos.getAsOffset().y & 1) == 0 ? 0.01f : 0.02f), pos.z + cursorOffset);
//        }
//        /**
//         * The cursor real position is not updated on pulseMode.
//         */
////        if (listenerPulseIndex == -1) {
////            lastHexPos = tilePos;
////        }
////        } else {
////            enable--;
////        }
//    }
    @Override
    public void cleanup() {
        super.cleanup();
        rayCastControl.clearRayDebug();
        listenerPulseIndex = -1;
        app.getInputManager().removeListener(tileActionListener);
    }
}
