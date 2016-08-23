package org.hexgridapi.core.mousepicking;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import java.util.ArrayList;
import org.hexgridapi.core.AbstractHexGridAppState;
import org.hexgridapi.core.data.HexTile;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.events.MouseInputEvent.MouseInputEventType;
import org.hexgridapi.events.MouseRayListener;
import org.hexgridapi.events.Registerable;
import org.hexgridapi.events.TileInputListener;
import org.slf4j.LoggerFactory;

/**
 * Take care of all mouse input happening on the grid.
 *
 * @author Eike Foede, roah
 */
public class GridMouseControlAppState extends AbstractAppState implements Registerable<MouseRayListener> {

    private Application app;
    private GridRayCastControl rayCastControl;
    private final ArrayList<TileInputListener> inputListeners = new ArrayList<>();
    private final ArrayList<MouseRayListener> rayListeners = new ArrayList<>(3);
    private final TileSelectionControl tileSelectionControl = new TileSelectionControl();
    private MouseRayListener listenerLockPulse = null;
    private Vector2f lastScreenMousePos = new Vector2f(0, 0);
    private MapData mapData;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.app = app;
        AbstractHexGridAppState hexGrid = app.getStateManager().getState(AbstractHexGridAppState.class);
        mapData = hexGrid.getMapData();
        /**
         * Activate the input to interact with the grid.
         */
        app.getInputManager().addListener(tileActionListener,
                new String[]{MouseInputEventType.LMB.toString(), MouseInputEventType.RMB.toString()});
//        app.getInputManager().addListener(tileActionListener, new String[]{"Confirm", "Cancel"});
        /**
         * Activate the RaycastDebug and the selection system (cursor).
         */
        rayCastControl = new GridRayCastControl(app, hexGrid.getBuilder().getBuilderNode(), ColorRGBA.Red);
        tileSelectionControl.initialise(app);
        register(tileSelectionControl);
    }

    // <editor-fold defaultstate="collapsed" desc=" Registers">
    /**
     * Register a listener to respond to Tile Input.
     *
     * @param listener to register.
     */
    public void register(TileInputListener listener) {
        inputListeners.add(listener);
    }

    /**
     * Remove a listener from responding to Tile Input.
     *
     * @param listener to register.
     */
    public void unregister(TileInputListener listener) {
        inputListeners.remove(listener);
    }

    /**
     * @param mouseInput L or R listener to call
     * @param event event to pass
     */
    private void callMouseInputActionListeners(MouseInputEvent event) {
        inputListeners.stream().forEach((l) -> {
            l.onMouseAction(event);
        });
    }

    /**
     * Register a listener to respond from Raycasting event.
     *
     * @param listener
     */
    @Override
    public void register(MouseRayListener listener) {
        rayListeners.add(listener);
        inputListeners.add(listener);
    }

    /**
     * Remove a listener from responding Raycasting event. <br>
     * Disable pulseMode if the listeners is the one who activated it.
     *
     * @param listener
     */
    @Override
    public void unregister(MouseRayListener listener) {
        rayListeners.remove(listener);
        inputListeners.remove(listener);
        if(listenerLockPulse != null) {
            setCursorPulseMode(listener);
        }
    }

    /**
     * Used to bypass the default ray.
     *
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
    // </editor-fold>
    private final ActionListener tileActionListener = (String name, boolean isPressed, float tpf) -> {
        if (listenerLockPulse == null) {
            if (name.equals(MouseInputEventType.LMB.toString()) && !isPressed) {
                castRay(MouseInputEventType.LMB);
            } else if (name.equals(MouseInputEventType.RMB.toString()) && !isPressed) {
                castRay(MouseInputEventType.RMB);
            }
        } else {
//            listenerLockPulse.onMouseAction(
//                    new MouseInputEvent(MouseInputEventType.PULSE, tileSelectionControl.getSelectedPos(),
//                            mapData.getTile(tileSelectionControl.getSelectedPos()).getHeight(),
//                            rayCastControl.get3DRay(GridRayCastControl.CastFrom.MOUSE), null));
        }
    };

    @Override
    public void update(float tpf) {
        if (listenerLockPulse != null) {
            Vector2f newMousePos = app.getInputManager().getCursorPosition().normalize();
            if (!newMousePos.equals(lastScreenMousePos)) {
                castRay(MouseInputEventType.PULSE);
                lastScreenMousePos = newMousePos;
            }
        }
    }

    /**
     * Only use to disable pulse mode. <br>
     * Not required to be call if also using <br>
     * {@link #unregister(MouseRayListener listener)}
     * 
     * @param listener listeners locking the pulse currently
     * @return false if the pulse is not currently activated or activated by another listeners.
     */
    public boolean setCursorPulseModeOff(MouseRayListener listener) {
        if(listenerLockPulse != null) {
            return setCursorPulseMode(listener);
        }
        return false;
    }
    
    /**
     * Activate / desactivate pulse mode, Raycast will follow the mouse, Have to
     * be called by the the same listener to be disabled. The pulse mode lock other
     * update.
     *
     * @todo Ray listener support
     * @param listener calling for it.
     * @return false if an error happen or if already on pulseMode.
     */
    public boolean setCursorPulseMode(MouseRayListener listener) {
        if (listenerLockPulse == null) {
            //We keep track of the listener locking the input.
            listenerLockPulse = listener;
            if (initialized) {
                lastScreenMousePos = app.getInputManager().getCursorPosition();
            }
            return true;
        } else if (listenerLockPulse.equals(listener)) {
            /**
             * We check if the listener calling the pulseMode is the same than
             * the one who activated it. if it is the same we desable the pulse
             * mode.
             */
            listenerLockPulse = null;
            return true;
        } else {
            LoggerFactory.getLogger(GridMouseControlAppState.class).warn("Pulse already locked by : {} , Lock requested by : {}",
                    listenerLockPulse.getClass().toString(), listener.toString());
            return false;
        }
    }

    private void castRay(MouseInputEventType mouseInput) {
        Ray ray = rayCastControl.get3DRay(GridRayCastControl.CastFrom.MOUSE);
        MouseInputEvent event;
        if (!mouseInput.equals(MouseInputEventType.PULSE)) {
            event = callRayActionListeners(mouseInput, ray);
            if (event == null) {
                event = rayCastControl.castRay(ray);
                if (event != null) { // && !event.getEventPosition().equals(lastHexPos)) {
                    HexTile tile = mapData.getTile(event.getPosition());
                    callMouseInputActionListeners(event.clone(mouseInput, tile != null ? tile.getHeight() : 0));
                }
            } else {// if (!event.getEventPosition().equals(lastHexPos)) {
                rayCastControl.setDebugPosition(event.getCollisionResult().getContactPoint());
                callMouseInputActionListeners(event);
            }
        } else {
            event = rayCastControl.castRay(ray);
            if (event != null) {
                HexTile tile = mapData.getTile(event.getPosition());
                event = event.clone(mouseInput, tile != null ? tile.getHeight() : 0);
                tileSelectionControl.onMouseAction(event);
                listenerLockPulse.onMouseAction(event);
            }
        }
    }

    public TileSelectionControl getSelectionControl() {
        return tileSelectionControl;
    }

    @Override
    public void cleanup() {
        super.cleanup();
        rayCastControl.removeDebug();
        listenerLockPulse = null;
        app.getInputManager().removeListener(tileActionListener);
        tileSelectionControl.cleanup();
    }
}
