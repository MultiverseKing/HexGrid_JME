package org.hexgridapi.editor.core;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.shadow.DirectionalLightShadowFilter;

/**
 *
 * @author roah
 */
public class DefaultSetting {
    private final SimpleApplication app;
    private DirectionalLight sun;
    private FilterPostProcessor fpp;
    private AmbientLight ambient;
    private boolean lightInit = false;
    private DirectionalLightShadowFilter dlsf;
    private boolean lightEnabled = false;

    public DefaultSetting(SimpleApplication app) {
        this.app = app;
    }
    
    private void initializeLight() {
        /**
         * A white, directional light source.
         */
        sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
//        sun.setColor(new ColorRGBA(250, 250, 215, 1));
        sun.setColor(ColorRGBA.White);

        /* this shadow needs a directional light */
        fpp = new FilterPostProcessor(app.getAssetManager());
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
        dlsf = new DirectionalLightShadowFilter(app.getAssetManager(), SHADOWMAP_SIZE, 1);
        dlsf.setLight(sun);
//        dlsf.setEnabled(true);
//        fpp = new FilterPostProcessor(app.getAssetManager());
        fpp.addFilter(dlsf);

        /**
         * A white ambient light source.
         */
        ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
//        ambient.setColor(new ColorRGBA(255, 255, 255, .5f));
        
        lightInit = true;
    }
    
    public void addLight(){
        if(!lightInit) {
            initializeLight();
        }
        app.getRootNode().addLight(sun);
        dlsf.setEnabled(true);
        app.getViewPort().addProcessor(fpp);
        app.getRootNode().addLight(ambient);
        lightEnabled = true;
    }
    
    public void removeLight(){
        if(!lightInit) {
            return;
        }
        app.getRootNode().removeLight(sun);
        dlsf.setEnabled(false);
        app.getViewPort().removeProcessor(fpp);
        app.getRootNode().removeLight(ambient);
        lightEnabled = false;
    }
    
    public boolean isLightEnabled() {
        return lightEnabled;
    }
}
