package org.hexgridapi.core.control;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.control.chunkbuilder.DefaultBuilder;
import org.hexgridapi.core.mesh.GreddyMesher;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;

/**
 * Directly control the chunk geometry, all tiles geometry.
 *
 * @author roah
 */
public class ChunkControl extends AbstractControl {

    protected final DefaultBuilder builder;
    protected boolean onlyGround;
    protected Vector2Int chunkPosition;

    public ChunkControl(DefaultBuilder builder, Vector2Int chunkPosition, boolean onlyGround) {
        this.chunkPosition = chunkPosition;
        this.onlyGround = onlyGround;
        this.builder = builder;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial != null && spatial instanceof Node) {
            // initialize
            super.setSpatial(spatial);
            ((Node) spatial).attachChild(new Node("TILES.0|0"));
            update();
            ((Node) spatial).setLocalTranslation(getChunkWorldPosition(chunkPosition));
        } else if (spatial == null) {
            // cleanup
        } else {
            throw new UnsupportedOperationException("Provided spatial must be a Node.");
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    /**
     * update all tile on the chunk, following the data contained in mapData.
     * /!\ internal use.
     */
    public void update() {
        if (spatial == null) {
            return;
        }
        /**
         * remove the old tile from the chunk.
         */
        ((Node) ((Node) spatial).getChild("TILES.0|0")).detachAllChildren();
        /**
         * Generate the tile and attach them with the right texture.
         * 1 geometry by texture.
         */
        builder.getTiles((Node) ((Node) spatial).getChild("TILES.0|0"), false, chunkPosition);
    }

    public Vector2Int getChunkPosition() {
        return chunkPosition;
    }

    /**
     * Convert chunk position in hexMap to world unit.
     *
     * @param chunkPosition
     * @hint world unit position is the same than the node containing the chunk.
     * @return chunk world unit position.
     */
    public static Vector3f getChunkWorldPosition(Vector2Int chunkPosition) {
        if (HexSetting.CHUNK_SHAPE_TYPE.equals(GreddyMesher.ShapeType.SQUARE)) {
            return new Vector3f(chunkPosition.x * HexSetting.CHUNK_SIZE * HexSetting.HEX_WIDTH, 0,
                    (chunkPosition.y * HexSetting.CHUNK_SIZE) * (float) (HexSetting.HEX_RADIUS * 1.5));
        } else {
//            int originPosY =// ((chunkPosition.x & 1) != 0 ? HexSetting.CHUNK_SIZE + 1 * (chunkPosition.y < 0 ? -1 : 1) : 0)
//                     chunkPosition.y * (HexSetting.CHUNK_SIZE+2);
//            int originPosX = //((chunkPosition.x & 1) != 0 ? (chunkPosition.x < 0 ? 1 : -1) : 0)
//                     chunkPosition.x * (HexSetting.CHUNK_SIZE+1);// + ((chunkPosition.x & 1) != 0 ? 0 : 1));
////            HexCoordinate chunkCenter = new HexCoordinate();
//            System.err.println("ChunkPos = " + chunkPosition.x + ". World position = "+originPosX);
//            return new Vector3f(originPosX*HexSetting.HEX_WIDTH, 0, originPosY * (float) (HexSetting.HEX_RADIUS * 1.5));
            return new HexCoordinate(HexCoordinate.Coordinate.OFFSET, chunkPosition).toWorldPosition();
//            float chunkDiamX = (HexSetting.CHUNK_SIZE * 2 + 1) * HexSetting.HEX_WIDTH;
//            float chunkDiamY = (HexSetting.CHUNK_SIZE * 2 + 1) * HexSetting.HEX_RADIUS * 1.5f;
//            return new Vector3f(chunkPosition.x * chunkDiamX
//                    + ((chunkPosition.y & 1) == 0 ? 0 : HexSetting.HEX_WIDTH / 2),
//                    0, chunkPosition.y * chunkDiamY);
        }
    }
}