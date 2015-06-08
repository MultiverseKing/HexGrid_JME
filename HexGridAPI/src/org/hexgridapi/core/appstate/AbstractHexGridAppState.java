package org.hexgridapi.core.appstate;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.RenderManager;
import org.hexgridapi.core.HexGrid;
import org.hexgridapi.core.data.MapData;

/**
 * AppState inplementing the Hexgrid, can be used as starting point.
 *
 * @author roah
 */
public abstract class AbstractHexGridAppState extends HexGrid implements AppState {

    /**
     * Is initialized on the first initialization.
     */
    private boolean initialized = false;
    /**
     * If enabled the update will run.
     */
    private boolean enabled = true;

    /**
     *
     * @param mapData tile dataHandler of the grid.
     */
    public AbstractHexGridAppState(MapData mapData) {
        super(mapData);
    }

    public final void initialize(AppStateManager stateManager, Application app) {
//        mapData.registerChunkChangeListener(this);
//        mapData.registerTileChangeListener(this);
        super.initialise(app);
//        ((Node)app.getViewPort().getScenes().get(0)).attachChild(gridNode);
        initializeSystem(stateManager, app);
        initialized = true;
    }

    public abstract void initializeSystem(AppStateManager stateManager, Application app);

    public boolean isInitialized() {
        return initialized;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public final void update(float tpf) {
        if (initialized && enabled) {
            updateSystem(tpf);
        }
    }

    public abstract void updateSystem(float tpf);

    public void stateAttached(AppStateManager stateManager) {
    }

    public void stateDetached(AppStateManager stateManager) {
    }

    public void render(RenderManager renderManager) {
    }

    public void postRender() {
    }

    @Override
    public final void cleanup() {
//        gridNode.removeFromParent();
        cleanupSystem();
        initialized = false;
        enabled = false;
        super.cleanup();
    }

    public abstract void cleanupSystem();
}
