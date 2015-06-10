package org.hexgridapi.core.data;

import org.hexgridapi.core.geometry.builder.ChunkCoordinate;
import com.jme3.asset.AssetManager;
import org.hexgridapi.events.TileChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.data.procedural.ProceduralHexGrid;
import org.hexgridapi.core.geometry.builder.GridParam;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.loader.HexGridMapLoader;
import org.hexgridapi.core.geometry.builder.coordinate.HexCoordinate;

/**
 * This class holds the hex data of the map.
 * When setting textureKey avoid using "NO_TILES" && "SELECTION_TEXTURE" &&
 * "EMPTY_TEXTURE_KEY"
 * these are used internaly as the Key index is :
 * -2 = and below used for non existant tile or ghost tile. StringKey ==
 * "NO_TILES"
 * -1 = used for areaTexture. StringKey == "SELECTION_TEXTURE"
 * 00 = default tecture used when specifiating no texture. StringKey ==
 * "EMPTY_TEXTURE_KEY"
 * 01 = inclusive and above used for user added texture (ordered the way the get
 * added).
 *
 * @author Eike Foede, Roah
 */
public final class MapData {

    private final ChunkData chunkData = new ChunkData();
    private final ArrayList<TileChangeListener> tileListeners = new ArrayList<TileChangeListener>();
    private final HexGridMapLoader hexGridMapLoader;
    private final GridParam gridParameters;
    private String mapName = "Undefined";

    /**
     * Create a new instance of data for the map.
     *
     * @param textureKeys list of texture to use.
     * @param assetManager used to load the texture.
     * @param generator if using procedural generation.
     */
    public MapData(AssetManager assetManager, GridParam param) {//, GhostMode mode) {
        hexGridMapLoader = new HexGridMapLoader(assetManager);
        this.gridParameters = param;
    }

    /**
     * Register a listener to respond to tile event.
     *
     * @param listener to register.
     */
    public void registerTileChangeListener(TileChangeListener listener) {
        tileListeners.add(listener);
    }

    /**
     * Remove listener from responding to tile event.
     *
     * @param listener
     */
    public void removeTileChangeListener(TileChangeListener listener) {
        tileListeners.remove(listener);
    }

    /**
     * @return current map name.
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * Set the current map name.
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

//    public GhostMode getMode() {
//        return mode;
//    }
    /**
     *
     * @return the seed currently used for the procedural generation.
     */
    public int getSeed() {
        return gridParameters.getGenerator().getSeed();
    }

    public ProceduralHexGrid getGenerator() {
        return gridParameters.getGenerator();
    }

    /**
     * Generating a new seed cause the map to be reset.
     */
    public void generateNewSeed() {
        Cleanup();
        for (TileChangeListener l : tileListeners) {
            l.onGridReload();
        }
        gridParameters.getGenerator().setSeed(ProceduralHexGrid.generateSeed());
    }

    /**
     * Check if there is any data currently stored.
     *
     * @return true if there is data.
     */
    public boolean containTilesData() {
        return !chunkData.isEmpty();
    }

    /**
     * Check if the specifiate chunk got any tile data stored.
     *
     * @param chunkPos inspected chunk.
     * @return true the specifiate chunk got any tile stored.
     */
    public boolean contain(ChunkCoordinate chunkPos) {
        return chunkData.contain(chunkPos);
    }

    /**
     * Get tile(s) properties. (one tile)
     *
     * @param tilePos position of the tile.
     * @return can be null
     */
    public HexTile getTile(HexCoordinate tilePos) {
        HexTile t = chunkData.getTile(tilePos);
        if (t == null && gridParameters.getGenerator() != null) {
            t = gridParameters.getGenerator().getTileValue(tilePos);
        }
        return t;
    }

    /**
     * Get tile(s) properties. (multiple tile)
     *
     * @param tilePos position of the tile.
     * @return can contain null
     */
    public HexTile[] getTile(HexCoordinate[] tilePos) {
        HexTile[] result = new HexTile[tilePos.length];
        for (int i = 0; i < tilePos.length; i++) {
            result[i] = chunkData.getTile(tilePos[i]);
            if(result[i] != null && result[i].textureKey == getTextureKey(DefaultTextureValue.NO_TILE)){
                result[i] = null;
            }
            if (result[i] == null && gridParameters.getGenerator() != null) {
                result[i] = gridParameters.getGenerator().getTileValue(tilePos[i]);
            }
        }
        return result;
    }

    /**
     * Change the designed tile(s) properties.
     *
     * @param tilePos position of the tile to change.
     * @param tile tile to change.
     */
    public void setTile(HexCoordinate tilePos, HexTile tile) {
        setTile(new HexCoordinate[]{tilePos}, new HexTile[]{tile});
    }

    /**
     * Change the designed tile(s) properties. <br>
     * If {
     *
     * @param tileValue} size == 1 the given properties will be apply on all
     * position, else the two array size must match.
     *
     * @param tilePos position of the tile to change.
     * @param tileValue tile to change.
     */
    public void setTile(HexCoordinate[] tilePos, HexTile... tileValue) {
        TileChangeEvent[] tceList = new TileChangeEvent[tilePos.length];
        boolean arrayUpdate = false;
        if (tileValue.length > 1 && tileValue.length == tilePos.length) {
            arrayUpdate = true;
        } else if (tileValue.length > 1 && tileValue.length != tilePos.length) {
            throw new UnsupportedOperationException("Inserted param does not match "
                    + "the requiment as : if(tile.length > 1) tile.length == tilePos.length");
        }
        for (int i = 0; i < tilePos.length; i++) {
            tceList[i] = updateTileData(tilePos[i], arrayUpdate ? tileValue[i] : tileValue[0]);
        }
        updateTileListeners(tceList);
    }

//    public void setTilesHeight(byte[] height, HexCoordinate[] tilePos) {
//        TileChangeEvent[] tceList = new TileChangeEvent[tilePos.length];
//        for(int i = 0; i < tilePos.length; i++){
//            HexTile tile = tileData.get(tilePos[i].toOffset());
//            if (tile != null) {
//                tceList[i] = updateTileData(tilePos[i], tile.cloneChangedHeight(height));
//            } else {
//                tceList[i] = updateTileData(tilePos[i], new HexTile(height, defaultkeyTexture));
//            }
//        }
//        updateTileListeners(tceList);
//    }
//
//    public void setTilesTextureKey(String[] key, HexCoordinate... tilePos) {
//        TileChangeEvent[] tceList = new TileChangeEvent[tilePos.length];
//        for(int i = 0; i < tilePos.length; i++){
//            HexTile tile = tileData.get(tilePos[i].toOffset());
//            if (tile != null) {
//                tceList[i] = updateTileData(tilePos[i], tile.cloneChangedTextureKey(getTextureKey(key)));
//            } else {
//                tceList[i] = updateTileData(tilePos[i], new HexTile((byte) 0, getTextureKey(key)));
//            }
//        }
//        updateTileListeners(tceList);
//    }
    private TileChangeEvent updateTileData(HexCoordinate tilePos, HexTile tile) {
        HexTile oldTile;
        if (tile != null) {
            oldTile = chunkData.add(tilePos, 
                    tile.height > HexSetting.WATER_LEVEL 
                    ? tile : new HexTile(0, getTextureKey(DefaultTextureValue.NO_TILE)));
            if (oldTile != null && gridParameters.getGenerator() != null
                    && oldTile.getTextureKey() == getTextureKey(DefaultTextureValue.NO_TILE)) {
                chunkData.remove(tilePos);
                tile = gridParameters.getGenerator().getTileValue(tilePos);
                oldTile = null;
            }
        } else if (chunkData.contain(tilePos)) {
            oldTile = chunkData.remove(tilePos);
        } else if (!chunkData.contain(tilePos)
                && gridParameters.getGenerator() != null) {
            oldTile = chunkData.add(tilePos, new HexTile(0, getTextureKey(DefaultTextureValue.NO_TILE)));
        } else {
            oldTile = null;
        }
        return new TileChangeEvent(tilePos, oldTile, tile);
    }

    /**
     * @todo
     */
    public boolean saveArea(String mapName) {
        this.mapName = mapName;
        return hexGridMapLoader.saveArea(mapName);
    }

    /**
     * @todo
     */
    public boolean loadArea(String mapName) {
        return hexGridMapLoader.loadArea(mapName);
    }

//    /**
//     * The texture used when adding a tile with no texture.
//     *
//     * @param Key texture to use as default
//     * @todo
//     */
//    public void setDefaultTexture(String Key) {
//        for (Vector2Int coord : tileData.keySet()) {
//            tileData.put(coord, tileData.get(coord).cloneChangedTextureKey(getTextureKey(Key)));
//        }
//        updateTileListeners(new TileChangeEvent(null, new HexTile(Integer.MIN_VALUE, getTextureKey(Key)), new HexTile(Integer.MIN_VALUE, Integer.MIN_VALUE)));
//    }
    /**
     * Get all tile around the defined position, return null for tile who
     * doesn't exist.
     * <li> HexTile[0] == right </li>
     * <li> HexTile[1] == top right </li>
     * <li> HexTile[2] == top left </li>
     * <li> HexTile[3] == left </li>
     * <li> HexTile[4] == bot left </li>
     * <li> HexTile[5] == bot right </li>
     *
     * @param position of the center tile.
     * @return All tile arround the needed tile.
     */
    public HexTile[] getNeightbors(HexCoordinate position) {
        HexCoordinate[] coords = position.getNeighbours();
        HexTile[] neighbours = new HexTile[coords.length];
        for (int i = 0; i < neighbours.length; i++) {
            neighbours[i] = chunkData.getTile(coords[i]);
        }
        return neighbours;
    }

    /**
     * Call/Update all registered tile listener with the last event.
     *
     * @param tce Last tile event.
     */
    private void updateTileListeners(TileChangeEvent... tce) {
        for (TileChangeListener l : tileListeners) {
            l.onTileChange(tce);
        }
    }

    /**
     * Cleanup the current map.
     */
    public void Cleanup() {
        //Todo remove all file from the temps folder
        chunkData.clear();
    }

    /**
     * Convert a textureKey to is mapped value (name).
     *
     * @param textureKey
     * @return EMPTY_TEXTURE_KEY if not found
     */
    public String getTextureValue(int textureKey) {
        if (textureKey <= -1) {
            return "NO_TILE";
        } else {
            try {
                return gridParameters.getTextureKeys().get(textureKey);
            } catch (IndexOutOfBoundsException e) {
                return "EMPTY_TEXTURE_KEY";
            }
        }
    }

    /**
     * Convert a texture value (name) to it's mapped textureKey.
     *
     * @param value texture name
     * @return "NO_TILE" if == null
     * @throws NoSuchFieldError if no mapping
     */
    public int getTextureKey(String value) throws NoSuchFieldError {
        if (value == null || value.equals("NO_TILE")) {
            return -1;
        }
        int result = gridParameters.getTextureKeys().indexOf(value);
        if (result == -1) {
            throw new NoSuchFieldError(value + " is not in the registered key List.");
        } else {
            return result;
        }
    }

    public int getTextureKey(DefaultTextureValue value) throws NoSuchFieldError {
        return getTextureKey(value.toString());
    }

    /**
     *
     * @return all registered texture value. (read only)
     */
    public List<String> getTextureKeys() {
        return Collections.unmodifiableList(gridParameters.getTextureKeys());
    }

    /**
     * @return The currently used Parameters.
     */
    public GridParam getGridParameters() {
        return gridParameters;
    }

    public enum DefaultTextureValue {

        NO_TILE,
        EMPTY_TEXTURE_KEY,
        SELECTION_TEXTURE
    }
}