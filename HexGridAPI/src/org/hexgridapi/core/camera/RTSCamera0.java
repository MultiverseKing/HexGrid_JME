package org.hexgridapi.core.camera;

import org.hexgridapi.core.geometry.buffer.BufferPositionProvider;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.bounding.BoundingVolume;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.hexgridapi.core.data.HexTile;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.geometry.HexSetting;
import org.hexgridapi.utility.Vector2Int;

/**
 *
 * <pre>
 * getStateManager().detach(getStateManager().getState(FlyCamAppState.class));
 * RtsCam rtsCam = new RtsCam(UpVector.Y_UP);
 * rtsCam.setCenter(new Vector3f(0, 0, 0));
 * rtsCam.setDistance(200);
 * rtsCam.setMaxSpeed(DoF.FWD, 100, 0.5f);
 * rtsCam.setMaxSpeed(DoF.SIDE, 100, 0.5f);
 * rtsCam.setMaxSpeed(DoF.DISTANCE, 100, 0.5f);
 * rtsCam.setHeightProvider(new HeightProvider() {
 *     public float getHeight(Vector2f coord) {
 *         return terrain.getHeight(coord)+10;
 *     }
 * });
 * getStateManager().attach(rtsCam);
 * </pre>
 *
 * @author Artur Biesiadowski, Roah
 *
 */
public final class RTSCamera0 extends HexGridCamera implements BufferPositionProvider {

    /**
     * Degree of Freedom
     *
     */
    public enum DoF {

        SIDE,
        FWD,
        ROTATE,
        TILT,
        DISTANCE;
    }

    public enum UpVector {

        Y_UP(Vector3f.UNIT_Y),
        Z_UP(Vector3f.UNIT_Z);
        final Vector3f upVector;

        UpVector(Vector3f upVector) {
            this.upVector = upVector;
        }
    }

    public interface HeightProvider {

        public float getHeight(Vector2f coord);
    }
    private InputManager inputManager;
    private BoundingVolume centerBounds;
    private BoundingVolume cameraBounds;
    private final int[] direction = new int[5];
    private final float[] accelTime = new float[5];
    private final float[] offsetMoves = new float[5];
    private final float[] maxSpeedPerSecondOfAccell = new float[5];
    private final float[] maxAccellPeriod = new float[5];
    private final float[] decelerationFactor = new float[5];
    private final float[] minValue = new float[5];
    private final float[] maxValue = new float[5];
    private final Vector3f position = new Vector3f();
    private final Vector3f center = new Vector3f();
    private final InternalListener listener = new InternalListener();
    private final UpVector up;
    private final Vector3f oldPosition = new Vector3f();
    private final Vector3f oldCenter = new Vector3f();
    private final Vector2f tempVec2 = new Vector2f();
    private float rot = -FastMath.PI;
    private float distance = 10;
    private HeightProvider heightProvider;
    private boolean wheelEnabled = true;
    private static final int SIDE = DoF.SIDE.ordinal();
    private static final int FWD = DoF.FWD.ordinal();
    private static final int ROTATE = DoF.ROTATE.ordinal();
    private static final int DISTANCE = DoF.DISTANCE.ordinal();
    private static final float WHEEL_SPEED = 1f / 15;
    private static String[] mappings = new String[]{
        "+SIDE", "+FWD", "+ROTATE", "-SIDE", "-FWD", "-ROTATE", "+WHEEL", "-WHEEL", "MOUSE_ROTATE"};
    private KeyMapping keyMapping;
    private int camRotOrigin = 120;
    private int camDistanceOrigin = 25;
    private MapData mapData;
    private boolean rotateFromMouse = false;

    public RTSCamera0(KeyMapping keyMapping) {
        this.up = UpVector.Y_UP;
        this.keyMapping = keyMapping;

        setMinMaxValues(DoF.SIDE, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
        setMinMaxValues(DoF.FWD, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
        setMinMaxValues(DoF.ROTATE, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
        setMinMaxValues(DoF.TILT, 0.2f, (float) (Math.PI / 2) - 0.001f);
        setMinMaxValues(DoF.DISTANCE, 2, Float.POSITIVE_INFINITY);

        setMaxSpeed(DoF.SIDE, 10f, 0.4f);
        setMaxSpeed(DoF.FWD, 10f, 0.4f);
        setMaxSpeed(DoF.ROTATE, 2f, 0.4f);
        setMaxSpeed(DoF.TILT, 1f, 0.4f);
        setMaxSpeed(DoF.DISTANCE, 15f, 0.4f);
    }

    public void resetToOriginPosition(Vector3f originPos) {
        setCenter(originPos);
        setRot(camRotOrigin);
        setDistance(camDistanceOrigin);

        float sinRot = FastMath.sin(rot);
        float cosRot = FastMath.cos(rot);
        updatePosition(sinRot, cosRot);

        cam.setLocation(position);
        cam.lookAt(center, up.upVector);

        oldPosition.set(position);
        oldCenter.set(center);
    }

    /**
     *
     * @param stateManager
     * @param app
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.cam = app.getCamera();
        if (app instanceof SimpleApplication) {
            ((SimpleApplication) app).getFlyByCamera().setEnabled(false);
        }
        this.inputManager = app.getInputManager();
        registerWithInput(inputManager);
    }

    /**
     * Set the maximum speed for given direction of movement. For
     * SIDE/FWD/DISTANCE it is in units/second, for ROTATE/TILT it is in
     * radians/second. Deceleration time is assumed to be the same as
     * acceleration time.
     *
     * @param deg degree of freedom for which to set the maximum speed
     * @param maxSpd maximum speed of movement in that direction
     * @param accelTime amount of time which is need to accelerate to full speed
     * in seconds (has to be bigger than zero, values over half second feel very
     * sluggish). Defaults are 0.4 seconds
     */
    public void setMaxSpeed(DoF deg, float maxSpd, float accelTime) {
        setMaxSpeed(deg, maxSpd, accelTime, accelTime);
    }

    /**
     * Set the maximum speed for given direction of movement. For
     * SIDE/FWD/DISTANCE it is in units/second, for ROTATE/TILT it is in
     * radians/second.
     *
     * @param deg degree of freedom for which to set the maximum speed
     * @param maxSpd maximum speed of movement in that direction
     * @param accelTime amount of time which is need to accelerate to full speed
     * in seconds (has to be bigger than zero, values over half second feel very
     * sluggish). Defaults are 0.4 seconds
     * @param decelerationTime amount of time in seconds which is needed to
     * automatically decelerate (friction-like stopping) from maxSpd to full
     * stop.
     */
    public void setMaxSpeed(DoF deg, float maxSpd, float accelTime, float decelerationTime) {
        maxSpeedPerSecondOfAccell[deg.ordinal()] = maxSpd / accelTime;
        maxAccellPeriod[deg.ordinal()] = accelTime;
        if (decelerationTime < 0.00001) {
            decelerationTime = 0.00001f;
        }
        decelerationFactor[deg.ordinal()] = accelTime / decelerationTime;
    }

    /**
     * Set the terrain following logic for camera. Camera position will not get
     * under the value returned by the heightProvider. Please add some extra
     * buffering here, so camera will not clip the actual terrain - for example
     *
     * <pre>
     * new HeightProvider() {
     *     &#064;Override
     *     public float getHeight(Vector2f coord) {
     *         return terrain.getHeight(coord) + 10;
     *     }
     * }
     * </pre>
     *
     * @param heightProvider
     */
    public void setHeightProvider(HeightProvider heightProvider) {
        this.heightProvider = heightProvider;
    }

    public void setHeightProvider(MapData mapData) {
        if (mapData != null) {
            this.mapData = mapData;
            setHeightProvider(hexGridHeightProvider);
        }
    }
    private HeightProvider hexGridHeightProvider = new HeightProvider() {
        public float getHeight(Vector2f coord) {
            HexTile t = mapData.getTile(new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(coord)));
            return (t != null ? t.getHeight() + 10 : 10) * HexSetting.FLOOR_OFFSET;
        }
    };

    /**
     * Enables/disabled wheel-zoom behaviour Default is enabled
     *
     * @param wheelEnabled
     */
    public void setWheelEnabled(boolean wheelEnabled) {
        this.wheelEnabled = wheelEnabled;
    }

    private String mouseButtonName(int button) {
        switch (button) {
            case MouseInput.BUTTON_LEFT:
                return "BUTTON1";

            case MouseInput.BUTTON_MIDDLE:
                return "BUTTON2";

            case MouseInput.BUTTON_RIGHT:
                return "BUTTON3";
            default:
                return null;
        }
    }

    /**
     * Use MouseInput.BUTTON_ constants to indicate which buttons should be used
     * for rotation and dragging with mouse Defaults are BUTTON_MIDDLE for
     * rotation and BUTTON_RIGHT for dragging Use -1 to disable given
     * functionality
     *
     * @param rotationButton button to hold to control TILT/ROTATION with mouse
     * movements
     * @param dragButton button to hold to drag camera position around
     */
//    public void setMouseDragging(int rotationButton, int dragButton) {
//        mouseDragButton = mouseButtonName(dragButton);
//        mouseRotationButton = mouseButtonName(rotationButton);
//    }
    @Override
    public void update(final float tpf) {
        for (int i = 0; i < direction.length; i++) {
            int dir = direction[i];
            switch (dir) {
                case -1:
                    accelTime[i] = clamp(-maxAccellPeriod[i], accelTime[i] - tpf, accelTime[i]);
                    break;
                case 0:
                    if (accelTime[i] != 0) {
                        double oldSpeed = accelTime[i];
                        if (accelTime[i] > 0) {
                            accelTime[i] -= tpf * decelerationFactor[i];
                        } else {
                            accelTime[i] += tpf * decelerationFactor[i];
                        }
                        if (oldSpeed * accelTime[i] < 0) {
                            accelTime[i] = 0;
                        }
                    }
                    break;
                case 1:
                    accelTime[i] = clamp(accelTime[i], accelTime[i] + tpf, maxAccellPeriod[i]);
                    break;
            }
        }
        updateDistance(tpf);
        updateRotation(tpf);

        float sinRot = FastMath.sin(rot);
        float cosRot = FastMath.cos(rot);
        updateCenter(tpf, sinRot, cosRot);
        updatePosition(sinRot, cosRot);

        for (int i = 0; i < offsetMoves.length; i++) {
            offsetMoves[i] = 0;
        }

        if (oldPosition.equals(position) && oldCenter.equals(center)) {
            return;
        }

        if (cameraBounds != null) {
            //TODO: clamp position to bounds
        }

        cam.setLocation(position);
        cam.lookAt(center, up.upVector);

        oldPosition.set(position);
        oldCenter.set(center);

    }

    private void updateDistance(float tpf) {
        float distanceChange = maxSpeedPerSecondOfAccell[DISTANCE] * accelTime[DISTANCE] * tpf;
        distance += distanceChange;
        distance += offsetMoves[DISTANCE];
        distance = clamp(minValue[DISTANCE], distance, maxValue[DISTANCE]);
    }

    private void updateRotation(float tpf) {
        rot += maxSpeedPerSecondOfAccell[ROTATE] * accelTime[ROTATE] * tpf + offsetMoves[ROTATE];
        rot = clamp(minValue[ROTATE], rot % (FastMath.PI * 2), maxValue[ROTATE]);
    }

    private void updateCenter(float tpf, float sinRot, float cosRot) {
        double offX = maxSpeedPerSecondOfAccell[SIDE] * accelTime[SIDE] * tpf + offsetMoves[SIDE];
        double offY = maxSpeedPerSecondOfAccell[FWD] * accelTime[FWD] * tpf + offsetMoves[FWD];
        center.x += offX * cosRot + offY * sinRot;
        if (up == UpVector.Y_UP) {
            center.z += offX * -sinRot + offY * cosRot;
        } else {
            center.y += offX * -sinRot + offY * cosRot;
        }
        if (centerBounds != null) {
            //TODO: clamp center to bounds
        }
    }

    private void updatePosition(float sinRot, float cosRot) {
        if (up == UpVector.Y_UP) {
            position.x = center.x + distance * sinRot;
            position.y = center.y + distance;
            position.z = center.z + distance * cosRot;
            if (heightProvider != null) {
                float h = heightProvider.getHeight(tempVec2.set(position.x, position.z));
                if (position.y < h) {
                    position.y = h;
                }
            }
        } else {
            position.x = center.x + distance * sinRot;
            position.y = center.y + distance * cosRot;
            position.z = center.z + distance;

            if (heightProvider != null) {
                float h = heightProvider.getHeight(tempVec2.set(position.x, position.y));
                if (position.z < h) {
                    position.z = h;
                }
            }
        }
    }

    private static float clamp(float min, float value, float max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        } else {
            return value;
        }
    }

    public float getMaxSpeed(DoF dg) {
        return maxSpeedPerSecondOfAccell[dg.ordinal()];
    }

    public float getMinValue(DoF dg) {
        return minValue[dg.ordinal()];
    }

    public float getMaxValue(DoF dg) {
        return maxValue[dg.ordinal()];
    }

    /**
     * SIDE and FWD min/max values are ignored
     *
     * @param dg
     * @param min
     * @param max
     */
    public void setMinMaxValues(DoF dg, float min, float max) {
        minValue[dg.ordinal()] = min;
        maxValue[dg.ordinal()] = max;
    }

    public Vector3f getBufferPosition() {
        return center;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setCenter(Vector3f center) {
        this.center.set(center);
    }

    public Vector3f getCenter() {
        return center;
    }

    public float getDistance() {
        return distance;
    }

    public float getRot() {
        return rot;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setRot(float rot) {
        this.rot = rot;
    }

    private void registerWithInput(InputManager inputManager) {
        this.inputManager = inputManager;
        registerKeys();
        registerMouse();
        inputManager.addListener(listener, mappings);
    }

    private void registerMouse() {
        inputManager.addMapping("-WHEEL", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("+WHEEL", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addMapping("MOUSE_ROTATE", new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
    }
        
    private void registerMouseRotation() {
        inputManager.addMapping("-ROTATE", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("+ROTATE", new MouseAxisTrigger(MouseInput.AXIS_X, true));
    }
    private void unRegisterMouseRotation() {
        inputManager.deleteMapping("-ROTATE");
        inputManager.deleteMapping("+ROTATE");
    }

    private void registerKeys() {
        KeyTrigger negSide, posSide, negRot, posRot, negFWD, posFWD;
        if (keyMapping.equals(KeyMapping.fr)) {
            negSide = new KeyTrigger(KeyInput.KEY_Q);
            posSide = new KeyTrigger(KeyInput.KEY_D);
//            negRot = new KeyTrigger(KeyInput.KEY_E);
//            posRot = new KeyTrigger(KeyInput.KEY_A);
            negFWD = new KeyTrigger(KeyInput.KEY_Z);
            posFWD = new KeyTrigger(KeyInput.KEY_S);
        } else if (keyMapping.equals(KeyMapping.col)) {
            negSide = new KeyTrigger(KeyInput.KEY_A);
            posSide = new KeyTrigger(KeyInput.KEY_S);
//            negRot = new KeyTrigger(KeyInput.KEY_F);
//            posRot = new KeyTrigger(KeyInput.KEY_Q);
            negFWD = new KeyTrigger(KeyInput.KEY_W);
            posFWD = new KeyTrigger(KeyInput.KEY_R);
        } else {// (keyMapping.equals(KeyMapping.us)) {
            negSide = new KeyTrigger(KeyInput.KEY_A);
            posSide = new KeyTrigger(KeyInput.KEY_D);
//            negRot = new KeyTrigger(KeyInput.KEY_E);
//            posRot = new KeyTrigger(KeyInput.KEY_Q);
            negFWD = new KeyTrigger(KeyInput.KEY_W);
            posFWD = new KeyTrigger(KeyInput.KEY_S);
        }

        if (up == UpVector.Y_UP) {
            inputManager.addMapping("-SIDE", negSide);
            inputManager.addMapping("+SIDE", posSide);
//            inputManager.addMapping("+ROTATE", posRot);
//            inputManager.addMapping("-ROTATE", negRot);
        } else {
            inputManager.addMapping("-SIDE", posSide);
            inputManager.addMapping("+SIDE", negSide);
//            inputManager.addMapping("+ROTATE", negRot);
//            inputManager.addMapping("-ROTATE", posRot);
        }

        inputManager.addMapping("+FWD", posFWD);
        inputManager.addMapping("-FWD", negFWD);
    }

    @Override
    public void cleanup() {
        super.cleanup();

        for (String mapping : mappings) {
            if (inputManager.hasMapping(mapping)) {
                inputManager.deleteMapping(mapping);
            }
        }
        inputManager.removeListener(listener);
    }

    private class InternalListener implements ActionListener, AnalogListener {

        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (!isEnabled()) {
                return;
            }

            int press = isPressed ? 1 : 0;
            
            if (name.contains("WHEEL")) {
                return;
            } else if (name.contains("MOUSE")) {
                rotateFromMouse = !rotateFromMouse;
                if(rotateFromMouse) {
                    registerMouseRotation();
                } else {
                    unRegisterMouseRotation();
                }
                return;
            }

            char sign = name.charAt(0);
            if (sign == '-') {
                press = -press;
            } else if (sign != '+') {
                return;
            }

            DoF deg = DoF.valueOf(name.substring(1));
            direction[deg.ordinal()] = press;
        }

        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (!isEnabled()) {
                return;
            }

            if (!name.contains("WHEEL") && !name.contains("MOUSE")) {
                return;
            }
            
            System.err.println(name);

            char sign = name.charAt(0);
            if (sign == '-') {
                value = -value;
            } else if (sign != '+') {
                return;
            }

            if (name.contains("WHEEL")) {
                if (!wheelEnabled) {
                    return;
                }
                float speed = maxSpeedPerSecondOfAccell[DISTANCE] * maxAccellPeriod[DISTANCE] * WHEEL_SPEED;
                offsetMoves[DISTANCE] += value * speed;
            }

        }
    }

    public enum KeyMapping {

        us,
        fr,
        col
    }
}
