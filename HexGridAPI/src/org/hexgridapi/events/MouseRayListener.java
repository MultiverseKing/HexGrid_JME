package org.hexgridapi.events;

import com.jme3.math.Ray;
import org.hexgridapi.events.MouseInputEvent.MouseInputEventType;

/**
 * Inteface used to get user mouse events on the raycast level.
 * This is called before MouseInputListener.
 *
 * @author roah
 */
public interface MouseRayListener extends TileInputListener {

    /**
     * 
     * @param mouseInputType the inputs the user used to trigger the events.
     * @param ray the ray generated for this specifiaue events.
     * @return the result of the events (can be null)
     */
    MouseInputEvent MouseRayInputAction(MouseInputEventType mouseInputType, Ray ray);
}
