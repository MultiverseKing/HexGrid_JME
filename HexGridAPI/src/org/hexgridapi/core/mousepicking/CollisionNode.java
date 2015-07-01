package org.hexgridapi.core.mousepicking;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author roah
 * @deprecated ...
 */
public class CollisionNode extends Node {
    
    private final static String KEY = "org.hexgridapi.collision";
    
    public CollisionNode(String name) {
        super(name);
    }
    
    public void setCollision(boolean enable) {
        setCollision(this, enable);
    }

    private static void setCollision(Spatial spatial, boolean enable) {
        if(enable && spatial.getUserData(KEY) != null) {
            spatial.setUserData(KEY, null);
        } else if (!enable) {
            spatial.setUserData(KEY, false);
        }
        if(spatial instanceof Node) {
            updateCollisionChild((Node) spatial, enable);
        }
    }

    private static void updateCollisionChild(Node node, boolean enable) {
      for (int x = 0, max = node.getChildren().size(); x < max; x++) {
          setCollision(node.getChild(x), enable);
      }
    }

    @Override
    public int attachChild(Spatial child) {
        int val = super.attachChild(child);
        setCollision(child, (Boolean)getUserData(KEY));
        return val;
    }

    @Override
    public int attachChildAt(Spatial child, int index) {
        int val = super.attachChildAt(child, index);
        setCollision(child, (Boolean)getUserData(KEY));
        return val;
    }

    @Override
    public int detachChild(Spatial child) {
        int val = super.detachChild(child);
        if(val != -1) {
            setCollision(child, true);
        }
        return val;
    }

    @Override
    public int detachChildNamed(String childName) {
        if (childName == null)
            throw new NullPointerException();

        for (int x = 0, max = children.size(); x < max; x++) {
            Spatial child =  children.get(x);
            if (childName.equals(child.getName())) {
                setCollision(child, true);
                detachChildAt( x );
                return x;
            }
        }
        return -1;
    }

    @Override
    public Spatial detachChildAt(int index) {
        Spatial s = super.detachChildAt(index);
        setCollision(s, true);
        return s;
    }

    @Override
    public void detachAllChildren() {
        for ( int i = children.size() - 1; i >= 0; i-- ) {
            setCollision(getChild(i), true);
            detachChildAt(i);
        }
    }
}
