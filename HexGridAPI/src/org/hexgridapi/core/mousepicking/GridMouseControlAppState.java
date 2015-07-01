package org.hexgridapi.core.mousepicking;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import java.util.ArrayList;
import org.hexgridapi.core.data.HexTile;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.AbstractHexGridAppState;
import org.hexgridapi.events.Register;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.events.MouseInputEvent.MouseInputEventType;
import org.hexgridapi.events.TileInputListener;
import org.hexgridapi.events.MouseRayListener;
import org.slf4j.LoggerFactory;

/**
 * Take care of all mouse input happening on the grid.
 *
 * @author Eike Foede, roah
 */
public class GridMouseControlAppState extends AbstractAppState implements Register<MouseRayListener> {

    private Application app;
    private GridRayCastControl rayCastControl;
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
        AbstractHexGridAppState hexGrid = app.getStateManager().getState(AbstractHexGridAppState.class);
        mapData = hexGrid.getMapData();
        /**
         * Activate the input to interact with the grid.
         */
        app.getInputManager().addListener(tileActionListener, new String[]{"Confirm", "Cancel"});
        /**
         * Activate the RaycastDebug.
         */
        rayCastControl = new GridRayCastControl(app, hexGrid.getBuilder().getBuilderNode(), ColorRGBA.Red);
        tileSelectionControl.initialise(app);
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
        for (TileInputListener l : inputListeners) {
            l.onMouseAction(event);
        }
    }

    /**
     * Register a listener to respond from Raycasting event.
     *
     * @param listener
     */
    public void register(MouseRayListener listener) {
        rayListeners.add(listener);
        inputListeners.add(listener);
    }

    /**
     * Remove a listener from responding Raycasting event.
     *
     * @param listener
     */
    public void unregister(MouseRayListener listener) {
        rayListeners.remove(listener);
        inputListeners.remove(listener);
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
                LoggerFactory.getLogger(GridMouseControlAppState.class).warn("Pulse already locked by : {} , Lock requested by : {}",
                        inputListeners.get(listenerPulseIndex).getClass().toString(), listener.toString());
                return false;
            } else {
                LoggerFactory.getLogger(GridMouseControlAppState.class).warn("Listener not registered :  {}.",
                        listener.toString());
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
            tileSelectionControl.getInputListener().onMouseAction(rayCastControl.castRay(ray));
        }
    }

    public TileSelectionControl getSelectionControl() {
        return tileSelectionControl;
    }

    @Override
    public void cleanup() {
        super.cleanup();
        rayCastControl.removeDebug();
        listenerPulseIndex = -1;
        app.getInputManager().removeListener(tileActionListener);
    }
}
