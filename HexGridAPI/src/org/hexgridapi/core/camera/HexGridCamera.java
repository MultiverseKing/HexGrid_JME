package org.hexgridapi.core.camera;

import com.jme3.app.state.AbstractAppState;
import com.jme3.renderer.Camera;

/**
 * @todo handle other type of camera than RTS
 * 
 * @author roah
 */
public abstract class HexGridCamera extends AbstractAppState {

    protected Camera cam;

    public Camera getCamera() {
        return cam;
    }
}
