package org.hexgridapi.core.appstate;

import com.jme3.app.SimpleApplication;
import org.hexgridapi.core.ApplicationParam;
import org.hexgridapi.core.RTSCamera;

/**
 *
 * @author roah
 */
public abstract class HexGridDefaultApplication extends SimpleApplication {

    private RTSCamera rtsCam;

    public RTSCamera getRtsCam() {
        return rtsCam;
    }

    @Override
    public void simpleInitApp() {
        super.inputManager.clearMappings();
        rtsCam = new ApplicationParam(this, false).getCam();
        initApp();
    }
    
    public abstract void initApp();
    
}
