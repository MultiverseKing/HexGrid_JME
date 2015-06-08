/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hexgridapi.core.geometry.builder.ghost;

import org.hexgridapi.core.geometry.builder.GhostChunkBuilder;
import com.jme3.app.Application;
import com.jme3.scene.Mesh;
import org.hexgridapi.core.geometry.builder.coordinate.SquareCoordinate;
import org.hexgridapi.core.geometry.builder.ChunkCoordinate;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.Set;
import org.hexgridapi.core.HexGrid;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.control.buffercontrol.ChunkBufferControl;
import org.hexgridapi.core.data.MapData;

/**
 * @author roah
 * @deprecated use SquareCoordinate
 */
public class SquareChunkBuilder extends GhostChunkBuilder {

    public SquareChunkBuilder(Application app, MapData mapData, ChunkBufferControl control) {
        super(app, mapData, control);
    }

    @Override
    public ArrayList<ChunkCoordinate> generateChunk() {
        ArrayList<ChunkCoordinate> list = new ArrayList<ChunkCoordinate>();
        for (int x = -HexSetting.GHOST_CONTROL_RADIUS; x <= HexSetting.GHOST_CONTROL_RADIUS; x++) {
            for (int y = -HexSetting.GHOST_CONTROL_RADIUS; y <= HexSetting.GHOST_CONTROL_RADIUS; y++) {
                if (!(x == 0 && y == 0)) {
                    list.add(new SquareCoordinate(x, y));
                }
            }
        }
        return list;
    }

    private void generate(HexGrid system, Node container, ChunkCoordinate chunkPosition, boolean onlyGround) {
        Set<ChunkCoordinate> list = chunksNodes.keySet();
        for (int x = -HexSetting.GHOST_CONTROL_RADIUS; x <= HexSetting.GHOST_CONTROL_RADIUS; x++) {
            for (int y = -HexSetting.GHOST_CONTROL_RADIUS; y <= HexSetting.GHOST_CONTROL_RADIUS; y++) {
                if (!(x == 0 && y == 0)
                        && !list.contains(chunkPosition.add(x, y))) {
//                        if (generatedChunk.contains(pos)) {
//                            //@todo
//                        } else {
                    genProcedural(x, y, chunkPosition, onlyGround, container);
//                        }
                }
            }
        }
    }

    @Override
    protected ArrayList<GhostControlCullingData> updateCulling(ChunkCoordinate chunkPosition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Mesh genCollisionPlane() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
