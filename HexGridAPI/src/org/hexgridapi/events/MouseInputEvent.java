package org.hexgridapi.events;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Ray;
import org.hexgridapi.core.coordinate.HexCoordinate;

/**
 *
 * @author roah
 */
public class MouseInputEvent {

    protected final HexCoordinate eventPosition;
    protected final Integer eventHeight;
    protected final Ray lastUsedRay;
    protected final CollisionResult collisionResult;
    protected final MouseInputEventType eventType;

    public MouseInputEvent(MouseInputEventType eventType, HexCoordinate eventPosition, Integer eventHeight, Ray usedRay, CollisionResult collisionResult) {
        this.eventPosition = eventPosition;
        this.lastUsedRay = usedRay;
        this.collisionResult = collisionResult;
        this.eventType = eventType;
        this.eventHeight = eventHeight;
    }

    public MouseInputEventType getType() {
        return eventType;
    }

    public HexCoordinate getPosition() {
        return eventPosition;
    }

    public int getHeight() {
        return eventHeight;
    }

    public Ray getLastUsedRay() {
        return lastUsedRay;
    }

    public CollisionResult getCollisionResult() {
        return collisionResult;
    }
    
    /**
     * Clone and replace parameter by the specifiate one.
     * 
     * @param eventType {@see MouseInputEventType}
     * @return 
     */
    public MouseInputEvent clone(MouseInputEventType eventType) {
        return new MouseInputEvent(eventType, eventPosition, eventHeight, lastUsedRay, collisionResult);
    }
    
    /**
     * Clone and replace parameter by the specifiate one.
     * 
     * @param eventHeight height of the tile.
     * @return 
     */
    public MouseInputEvent clone(Integer eventHeight) {
        return new MouseInputEvent(eventType, eventPosition, eventHeight, lastUsedRay, collisionResult);
    }

    /**
     * Clone and replace parameter by the specifiate one.
     *
     * @param eventType {@see MouseInputEventType}
     * @param eventHeight height of the tile.
     * @return 
     */
    public MouseInputEvent clone(MouseInputEventType eventType, Integer eventHeight) {
        return new MouseInputEvent(eventType, eventPosition, eventHeight, lastUsedRay, collisionResult);
    }

    /**
     * Mouse input currently supported. <br>
     * <p>
     * To create an event for the left mouse button {@link MouseInputEventType.LMB} <br>
     * To create an event for the right mouse button {@link MouseInputEventType.RMB} <br>
     * To create an event for the middle mouse button {@link MouseInputEventType.MMB} <br>
     * To create an event from the mouse each frame {@link MouseInputEventType.PULSE} <br>
     * </p>
     */
    public static enum MouseInputEventType {

        /**
         * Create an event for the left mouse bouton.
         */
        LMB,
        /**
         * Create an event for the right mouse bouton.
         */
        RMB,
        /**
         * Create an event for the middle mouse bouton.
         */
        MMB,
        /**
         * Create an event from the mouse each frame.
         */
        PULSE;
    }
}
