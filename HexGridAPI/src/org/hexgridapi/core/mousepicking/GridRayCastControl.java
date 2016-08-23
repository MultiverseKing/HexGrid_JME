package org.hexgridapi.core.mousepicking;

import com.jme3.app.Application;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import java.util.Iterator;
import org.hexgridapi.core.AbstractHexGridAppState;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.hexgridapi.core.geometry.buffer.HexGridBuffer;
import org.hexgridapi.events.MouseInputEvent;
import org.slf4j.LoggerFactory;

/**
 * Handle the mouse picking. Include the picking debug.
 * @author roah
 */
public class GridRayCastControl {

    private static final Node RAY_DEBUG_NODE = new Node(GridRayCastControl.class.getSimpleName()+ ":Node");
    private static Application app;
    private Node collisionNode;
    private Node rayDebug;

    /**
     * Handle the mouse picking.
     *
     * @param application currently running app.
     * @param collisionNode reference used by the ray to interact with.
     * @param debugColor used to show where the ray collide,<br>
     * if == null no debug. <br>
     * if != null debug is enabled but require {@link AbstractHexGridAppState}
     */
    public GridRayCastControl(Application application, Node collisionNode, ColorRGBA debugColor) {
        init(application, debugColor);
        this.collisionNode = collisionNode;
        if (debugColor != null) {
            initialiseDebug(debugColor);
        }
    }

    private void init(Application application, ColorRGBA debugColor) {
        if (app == null) {
            app = application;
        }
        if (debugColor != null && RAY_DEBUG_NODE.getParent() == null) {
            app.getStateManager().getState(AbstractHexGridAppState.class)
                    .getGridNode().attachChild(RAY_DEBUG_NODE);
        }
    }
    
    private void initialiseDebug(ColorRGBA debugColor) {
        Sphere sphere = new Sphere(30, 30, 0.2f);
        rayDebug = new Node("DebugRay");
        Geometry geo = new Geometry("SphereDebugRayCast" + debugColor, sphere);
//        geo.setUserData("org.hexgrid.collide", Boolean.TRUE);
        Material mark_mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", debugColor);
        geo.setMaterial(mark_mat);
        rayDebug.attachChild(geo);
        attachCoordinateAxes();
    }

    private void attachCoordinateAxes() {
        Vector3f pos = new Vector3f(0, 0.1f, 0);
        Arrow arrow = new Arrow(Vector3f.UNIT_X);
        arrow.setLineWidth(4); // make arrow thicker
        putShape(arrow, ColorRGBA.Red).setLocalTranslation(pos);

        arrow = new Arrow(Vector3f.UNIT_Y);
        arrow.setLineWidth(4); // make arrow thicker
        putShape(arrow, new ColorRGBA(0.2f, 1f, 0.2f, 1)).setLocalTranslation(pos);

        arrow = new Arrow(Vector3f.UNIT_Z);
        arrow.setLineWidth(4); // make arrow thicker
        putShape(arrow, new ColorRGBA(0.2f, 0.2f, 1, 1)).setLocalTranslation(pos);
    }

    private Geometry putShape(Mesh shape, ColorRGBA color) {
        Geometry g = new Geometry("coordinate axis", shape);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        rayDebug.attachChild(g);
        return g;
    }

    /**
     * Cast the defined ray on the defined collision node then return it
     * converted as Hex event.
     * @todo Does not work properly when picking a tile by the side
     * @param ray Can be get from {@link #get3DRay(CastFrom)}
     * @return null if no collision.
     */
    public MouseInputEvent castRay(Ray ray) {
        CollisionResults results = new CollisionResults();
        ray = ray != null ? ray : get3DRay(CastFrom.SCREEN_CENTER);
        collisionNode.collideWith(ray, results);
        if (results.size() != 0) {
            for (int i = 0; i < results.size(); i++) {
                CollisionResult closest = results.getCollision(i);
                if (!closest.getGeometry().getParent().getName().equals(HexGridBuffer.class.getName() + ":FX:Node")) {
                    setDebugPosition(closest.getContactPoint());
//                    HexCoordinate newPos = convertMouseCollision(results);
                    HexCoordinate newPos = new HexCoordinate(closest.getContactPoint());
                    return new MouseInputEvent(null, newPos, null, ray, closest);
                }
            }
            return null;
        } else {
            //Error catching.
            LoggerFactory.getLogger(GridRayCastControl.class).debug("Null raycast on {}", collisionNode);
            rayDebug.removeFromParent();
            return null;
        }
    }

    /**
     * Generate a new ray by converting 2d screen coordinates
     * to 3D world coordinates.
     *
     * @param from coordinate to use as parameter
     * @return Newly generated ray from parameter
     */
    public Ray get3DRay(CastFrom from) {
        Vector2f click2d;
        switch (from) {
            case MOUSE:
                click2d = app.getInputManager().getCursorPosition();
                break;
            case SCREEN_CENTER:
                click2d = new Vector2f(app.getCamera().getWidth() / 2, app.getCamera().getHeight() / 2);
                break;
            default:
                throw new UnsupportedOperationException(from + " isn't a valid type.");
        }
        Vector3f click3d = app.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = app.getCamera().getWorldCoordinates(
                new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
        Ray ray = new Ray(click3d, dir);
        return ray;
    }

    // @todo improve the picking for when clicking on the side of an hex
    private HexCoordinate convertMouseCollision(CollisionResults rayResults) {
        HexCoordinate tilePos;
        Vector3f pos;
        Iterator<CollisionResult> i = rayResults.iterator();

        pos = i.next().getContactPoint();
        tilePos = new HexCoordinate(pos);
        return tilePos;
    }

    /**
     * Make the ray have a new Collision reference.
     * This get ride of the current debug.
     *
     * @param collisionNode new reference.
     */
    public void setCollisionNode(Node collisionNode) {
        removeDebug();
        this.collisionNode = collisionNode;
    }

    public void setDebugPosition(Vector3f pos) {
        if (rayDebug != null) {
            RAY_DEBUG_NODE.attachChild(rayDebug);
            rayDebug.setLocalTranslation(pos);
        }
    }

    public enum CastFrom {

        /**
         * Current mouse position on screen.
         */
        MOUSE,
        /**
         * Always the center of the screen.
         */
        SCREEN_CENTER;
    }

    public void removeDebug() {
        rayDebug.removeFromParent();
    }

    public void cleanup() {
        removeDebug();
        if(collisionNode != null) {
            collisionNode.detachChild(rayDebug);
        }
    }
}
