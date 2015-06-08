package org.hexgridapi.core.control;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import org.hexgridapi.core.geometry.builder.ChunkBuilder;
import org.hexgridapi.core.geometry.builder.coordinate.HexCoordinate;
import org.hexgridapi.core.geometry.builder.ChunkCoordinate;

/**
 * Directly control the chunk geometry. aka : all geometry tiles.
 *
 * @author roah
 */
public class ChunkControl extends AbstractControl {

    protected final ChunkBuilder builder;
    protected ChunkCoordinate chunkPosition;

    public ChunkControl(ChunkBuilder builder, ChunkCoordinate chunkPosition) {
        this.chunkPosition = chunkPosition;
        this.builder = builder;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        setSpatial(spatial, true);
    }

    protected final void setSpatial(Spatial spatial, boolean update) {
        if (spatial != null && spatial instanceof Node) {
            // initialize
            super.setSpatial(spatial);
            ((Node) spatial).attachChild(new Node("TILES." + ChunkCoordinate.getNewInstance())); //@todo
            if (update) {
                updateChunk();
            }
            ((Node) spatial).setLocalTranslation(chunkPosition.getChunkOrigin().toWorldPosition());
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
     * updateChunk all tile on the chunk, following the data contained in
     * mapData.
     * /!\ internal use.
     */
    public void updateChunk() {
//        if (spatial == null) {
//            throw new RuntimeException("There is no Spatial to work with.");
//        }
        /**
         * remove the old tile from the chunk.
         */
        ((Node) ((Node) spatial).getChild("TILES." + ChunkCoordinate.getNewInstance())).detachAllChildren();
        /**
         * Generate the tile and attach them with the right texture.
         * 1 geometry by texture if not using texture Array.
         */
        builder.addChunkTo((Node) ((Node) spatial).getChild("TILES."
                + ChunkCoordinate.getNewInstance()), chunkPosition);
    }

    /**
     * @return the position of this chunk.
     */
    public ChunkCoordinate getChunkPosition() {
        return chunkPosition;
    }

    /**
     * Culling set to Inherit.
     */
    public void show() {
        spatial.setCullHint(Spatial.CullHint.Inherit);
    }

    /**
     * Culling set to Always.
     */
    public void hide() {
        spatial.setCullHint(Spatial.CullHint.Always);
    }

    /**
     * Hide all Ghost Tile
     * (GhostTile are tile who didn't have any data linked to them)
     * aka: if (tile == null) tile = ghostTile.
     * /!\ does not work when using GhostMode.Procedural
     */
    public void hideGhostTile(boolean hide) {
        Spatial geo = ((Node) ((Node) spatial).getChild("TILES." + ChunkCoordinate.getNewInstance())).getChild("NO_TILE");
        if (geo != null && hide) {
            geo.setCullHint(Spatial.CullHint.Always);
        } else if (geo != null) {
            geo.setCullHint(Spatial.CullHint.Inherit);
        }
    }
}