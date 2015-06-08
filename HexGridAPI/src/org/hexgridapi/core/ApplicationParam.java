package org.hexgridapi.core;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.shadow.DirectionalLightShadowFilter;
import org.hexgridapi.core.geometry.mesh.GreddyMesher;
import org.hexgridapi.utility.ArrowDebugShape;

/**
 *
 * @author roah
 */
public class ApplicationParam {

    private RTSCamera rtsCam;

    public ApplicationParam(SimpleApplication app, boolean debug) {
        app.setPauseOnLostFocus(false);
        lightSettup(app);
        cameraSettup(app);
        if (debug) {
            initDebug(app);
        }
    }

    private void lightSettup(SimpleApplication app) {
        /**
         * A white, directional light source.
         */
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(new ColorRGBA(250, 250, 215, 1));
        app.getRootNode().addLight(sun);

        /* this shadow needs a directional light */
        FilterPostProcessor fpp = new FilterPostProcessor(app.getAssetManager());
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
        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(app.getAssetManager(), SHADOWMAP_SIZE, 1);
        dlsf.setLight(sun);
        dlsf.setEnabled(true);
//        fpp = new FilterPostProcessor(app.getAssetManager());
        fpp.addFilter(dlsf);
        app.getViewPort().addProcessor(fpp);

        /**
         * A white ambient light source.
         */
//        AmbientLight ambient = new AmbientLight();
////        ambient.setColor(ColorRGBA.White);
//        ambient.setColor(new ColorRGBA(255, 255, 255, .5f));
//        app.getRootNode().addLight(ambient);
    }

    private void cameraSettup(SimpleApplication app) {
        app.getFlyByCamera().setEnabled(false);
        rtsCam = new RTSCamera(RTSCamera.UpVector.Y_UP, "AZERTY");
        rtsCam.setCenter(new Vector3f(20, 15, 18));
//        if (HexSetting.CHUNK_SHAPE_TYPE.equals(GreddyMesher.ShapeType.SQUARE)) {
//        } else {
//            rtsCam.setCenter(new Vector3f(11, 15, 15));
//        }
        rtsCam.setRot(120);
        app.getStateManager().attach(rtsCam);
    }

    private void initDebug(SimpleApplication app) {
        ArrowDebugShape arrowShape = new ArrowDebugShape(app.getAssetManager(), app.getRootNode(), new Vector3f(0f, 0f, 0f));
    }

    public RTSCamera getCam() {
        return rtsCam;
    }
}