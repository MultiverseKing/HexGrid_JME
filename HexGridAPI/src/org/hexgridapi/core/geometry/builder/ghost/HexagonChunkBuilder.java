package org.hexgridapi.core.geometry.builder.ghost;

import org.hexgridapi.core.geometry.builder.GhostChunkBuilder;
import com.jme3.app.Application;
import com.jme3.scene.Mesh;
import org.hexgridapi.core.geometry.builder.coordinate.HexagonCoordinate;
import org.hexgridapi.core.geometry.builder.ChunkCoordinate;
import com.jme3.scene.Node;
import java.util.ArrayList;
import org.hexgridapi.core.HexGrid;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.control.buffercontrol.ChunkBufferControl;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.geometry.builder.coordinate.HexCoordinate;

/**
 * @author roah
 * @deprecated use HexagonCoordinate
 */
public class HexagonChunkBuilder extends GhostChunkBuilder {

    public HexagonChunkBuilder(Application app, MapData mapData, ChunkBufferControl control) {
        super(app, mapData, control);
    }

    @Override
    public ArrayList<ChunkCoordinate> generateChunk() {
        ArrayList<ChunkCoordinate> list = new ArrayList<ChunkCoordinate>();
        HexCoordinate coord = new HexCoordinate();
        for (HexCoordinate c : coord.getCoordinateInRange(HexSetting.GHOST_CONTROL_RADIUS)) {
            list.add(new HexagonCoordinate(c.toCubic()));
        }
        return list;
    }
    
    public void generate(HexGrid system, Node container, ChunkCoordinate chunkPosition, boolean onlyGround, boolean procedural) {
        HexCoordinate coord = new HexCoordinate();
        for (HexCoordinate c : coord.getCoordinateInRange(HexSetting.GHOST_CONTROL_RADIUS)) {
            genProcedural(c.toOffset().x, c.toOffset().y, chunkPosition, onlyGround, container);
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
