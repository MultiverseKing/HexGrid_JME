package org.hexgridapi.core.geometry.buffer;

import org.hexgridapi.core.coordinate.BufferBuilder;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.List;
import java.util.Set;
import org.hexgridapi.core.ChunkCoordinate;
import org.hexgridapi.core.geometry.ChunkBuilder;
import org.hexgridapi.core.geometry.ChunkControl;
import org.hexgridapi.events.TileChangeEvent;

/**
 * Chunk handling by himself and follow the camera.
 *
 * @todo when the buffer move only generate needed chunk and not all.
 * @todo shadow...
 * @todo if chunk position is > 1 from center remove shadow !?
 * @author roah
 */
public final class BufferedChunk extends ChunkControl {

    private final Node bufferedChunkNode = new Node(BufferedChunk.class.getName() + ":Node");
    private List<ChunkCoordinate> persistantBufferList;
    private boolean useProcedural = false;

    public BufferedChunk(ChunkBuilder builder) {
        super(builder, ChunkCoordinate.getNewInstance());
        builder.getBuilderNode().attachChild(bufferedChunkNode);
        enabled = false;
    }

    public void setParam(Boolean useProcedural, int bufferRadius) {
        if(spatial != null) {
            spatial.removeControl(this);
        }
        persistantBufferList = getBufferedChunk(bufferRadius);
        this.useProcedural = useProcedural;
        bufferedChunkNode.addControl(this);
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial != null) {
            if (useProcedural) {
                super.setSpatial(spatial);
            } else {
                super.setSpatial(spatial, false);
            }
//            updateChunk();
            if (!useProcedural) {
                generateBufferedChunk();
            }
            enabled = true;
        } else {
            // cleanup
            bufferedChunkNode.detachAllChildren();
            persistantBufferList.clear();
            enabled = false;
        }
    }
    
    public final void onTileChange(TileChangeEvent... events) {
        if(enabled) {
            updateCulling();
        }
    }
    
    public void onPositionUpdate(ChunkCoordinate newBufferPosition) {
        chunkPosition = newBufferPosition;
        if(enabled) {
            if (useProcedural) {
                updateChunk();
                generateBufferedChunk();
            }
            updateCulling();
            spatial.setLocalTranslation(chunkPosition.getChunkOrigin().toWorldPosition());
        }
    }

    /**
     * Update the culling to avoid overlapping chunk.
     * (HexGrid chunk and this chunk is handled separetely)
     */
    public void updateCulling() {
        if(enabled) {
            Set<ChunkCoordinate> builderList = builder.getChunkList();
            persistantBufferList.add(ChunkCoordinate.getNewInstance());
            for (ChunkCoordinate c : persistantBufferList) {
                if (builderList.contains(c.add(chunkPosition))) {
                    updateCulling(c, true);
                } else {
                    updateCulling(c, false);
                }
            }
            persistantBufferList.remove(ChunkCoordinate.getNewInstance());
        }
    }

    private void updateCulling(ChunkCoordinate coord, boolean isCull) {
        if (useProcedural && isCull) {
            try {
                ((Node) ((Node) spatial).getChild("TILES." + coord)).detachAllChildren();
            } catch (NullPointerException ex) {
                throw new NullPointerException("TILES." + coord + " is not found");
            }
        } else if (!useProcedural && isCull) {
            ((Node) spatial).getChild("TILES." + coord).setCullHint(Spatial.CullHint.Always);
        } else if (!useProcedural) {
            ((Node) spatial).getChild("TILES." + coord).setCullHint(Spatial.CullHint.Inherit);
        }
    }

    /**
     * Generate all chunk arround the center.
     */
    private void generateBufferedChunk() {
        Geometry tmpVoidChunk = null;
        if (!useProcedural) {
            Node node = ((Node) ((Node) spatial).getChild("TILES." + ChunkCoordinate.getNewInstance()));
            builder.addVoidChunkTo(node);
            tmpVoidChunk = ((Geometry) node.getChild(0)).clone(true);
            tmpVoidChunk.getMaterial().setColor("Color", new ColorRGBA(0, 0.2f, 1f, 0.5f));
        }
        ChunkCoordinate coordZero = ChunkCoordinate.getNewInstance();
        for (ChunkCoordinate c : persistantBufferList) {
            if (!c.equal(coordZero)) {
                Node node = (Node) ((Node) spatial).getChild("TILES." + c);
                if (node == null) {
                    node = new Node("TILES." + c);
                    ((Node) spatial).attachChild(node);
                    node.setLocalTranslation(c.getChunkOrigin().toWorldPosition());
                } else if (useProcedural) {
                    node.detachAllChildren();
                }
                if (!useProcedural) {
                    node.attachChild(tmpVoidChunk.clone(false));
                } else {
                    builder.addChunkTo(node, chunkPosition.add(c), this);
                    if (builder.buildVoidTile() && !builder.showVoidTile()) {
                        hideVoidTile(true);
                    }
                }
            }
        }
    }

    private List<ChunkCoordinate> getBufferedChunk(int bufferRadius) {
        List<ChunkCoordinate> bufferList = ((BufferBuilder) chunkPosition)
                .getBufferedChunk(ChunkCoordinate.getNewInstance(), bufferRadius);
        bufferList.remove(ChunkCoordinate.getNewInstance());
        return bufferList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideVoidTile(boolean hide) {
    }

    /**
     * {@inheritDoc}
     */
    public void hideVoidTile(boolean hide, int bufferRadius) {
        for (ChunkCoordinate c : ((BufferBuilder) chunkPosition)
                .getBufferedChunk(ChunkCoordinate.getNewInstance(), bufferRadius)) {
            Spatial geo = ((Node) ((Node) spatial).getChild("TILES." + c)).getChild("NO_TILE");
            if (geo != null && hide) {
                geo.setCullHint(Spatial.CullHint.Always);
            } else if (geo != null) {
                geo.setCullHint(Spatial.CullHint.Inherit);
            }
        }
    }
}