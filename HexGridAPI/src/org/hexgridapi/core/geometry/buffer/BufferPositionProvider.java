package org.hexgridapi.core.geometry.buffer;

import com.jme3.math.Vector3f;

/**
 *
 * @author roah
 */
public interface BufferPositionProvider {
    Vector3f getBufferPosition();
    void resetToOriginPosition(Vector3f originPos);
}
