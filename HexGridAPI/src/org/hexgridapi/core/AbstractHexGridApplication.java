package org.hexgridapi.core;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.shadow.DirectionalLightShadowFilter;
import org.hexgridapi.utility.ArrowDebugShape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @todo fix shadow duplication
 * @author roah
 */
public abstract class AbstractHexGridApplication extends SimpleApplication {
    
    private static Logger apiLogger = LoggerFactory.getLogger("org.hexgridapi");

    @Override
    public final void simpleInitApp() {
        super.inputManager.clearMappings();
        setPauseOnLostFocus(false);
        lightSettup();
        
        initApp();
    }

    private void lightSettup() {
        /**
         * A white, directional light source.
         */
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
//        sun.setColor(new ColorRGBA(250, 250, 215, 1));
        sun.setColor(ColorRGBA.White);
        getRootNode().addLight(sun);

        /* this shadow needs a directional light */
        FilterPostProcessor fpp = new FilterPostProcessor(getAssetManager());
//        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(app.getAssetManager(), 1024, 1);
//        dlsf.setLight(sun);
//        fpp.addFilter(dlsf);

        /* AO */
//        FilterPostProcessor fpp = new FilterPostProcessor(app.getAssetManager());
//        SSAOFilter ssaoFilter = new SSAOFilter();//1f, 3.2f, 0.2f, 0.1f);
//        fpp.addFilter(ssaoFilter);
//        app.getViewPort().addProcessor(fpp);

//        /* DropShadow */
        final int SHADOWMAP_SIZE = 1024;
//        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(app.getAssetManager(), SHADOWMAP_SIZE, 1);
//        dlsr.setLight(sun);
//        app.getViewPort().addProcessor(dlsr);
// 
        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(getAssetManager(), SHADOWMAP_SIZE, 1);
        dlsf.setLight(sun);
        dlsf.setEnabled(true);
//        fpp = new FilterPostProcessor(app.getAssetManager());
        fpp.addFilter(dlsf);
        getViewPort().addProcessor(fpp);

        /**
         * A white ambient light source.
         */
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
//        ambient.setColor(new ColorRGBA(255, 255, 255, .5f));
        getRootNode().addLight(ambient);
    }

    private void initDebug() {
        ArrowDebugShape arrowShape = new ArrowDebugShape(getAssetManager(), getRootNode(), new Vector3f(0f, 0f, 0f));
    }
    
    public abstract void initApp();
}
