package org.hexgridapi.core.data;

import org.hexgridapi.core.geometry.HexSetting;
import com.jme3.asset.AssetManager;
import org.hexgridapi.events.MapDataListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hexgridapi.core.data.procedural.ProceduralHexGrid;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.loader.HexGridMapLoader;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.hexgridapi.events.Registerable;

/**
 * <h3>This class holds the hex data of the map. </h3>
 * When setting textureKey don't use : <br>
 * <b>"NO_TILES"</b> && <b>"SELECTION_TEXTURE"</b> &&
 * <b>"EMPTY_TEXTURE_KEY"</b><br>
 * <b>these are used internaly.</b> <br>
 * Current key index is : <br>
 * -2 = and below used for non existant tile or ghost tile.
 * <i>StringKey("NO_TILES")</i> <br>
 * -1 = used for areaTexture. <i>StringKey("SELECTION_TEXTURE")</i> <br>
 * 00 = default tecture used when specifiating no texture.
 * <i>StringKey("EMPTY_TEXTURE_KEY")</i> <br>
 * 01(inclusive) => used for user added texture (ordered the way the get added).
 * <br>
 *
 * @author Eike Foede, Roah
 */
public final class MapData implements Registerable<MapDataListener> {

    private final ChunkData chunkData = new ChunkData();
    private final ArrayList<MapDataListener> tileListeners = new ArrayList<MapDataListener>();
    private final ArrayList<String> textureKeys = new ArrayList<String>();
    private final HexGridMapLoader hexGridMapLoader;
    private ProceduralHexGrid generator;
    private String mapName = "Undefined";

    /**
     * Create a new instance of data for the map.
     *
     * @param assetManager internal use.
     * @param textureKeys list of texture to use.
     */
    public MapData(AssetManager assetManager, Object[] textureKeys) {
        hexGridMapLoader = new HexGridMapLoader(assetManager);

        this.textureKeys.add(MapData.DefaultTextureValue.EMPTY_TEXTURE_KEY.toString());
        if (textureKeys != null) {
            for (int i = 0; i < textureKeys.length; i++) {
                if (textureKeys[i].toString().equals(MapData.DefaultTextureValue.NO_TILE.toString())
                        || textureKeys[i].toString().equals(MapData.DefaultTextureValue.EMPTY_TEXTURE_KEY.toString())
                        || textureKeys[i].toString().equals(MapData.DefaultTextureValue.SELECTION_TEXTURE.toString())) {
                    throw new IllegalArgumentException(textureKeys[i] + " is not allowed.");
                }
                this.textureKeys.add(textureKeys[i].toString());
            }
        }
    }

    /**
     * Set the current map name.
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    /**
     * @return current map name.
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * Replace or disable the current generator. <br>
     * <pre>
     * <b>if({@param enable} == true && {@param generator} == null) : </b>
     * The default {@link ProceduralHexGrid} will be used.<br>
     * <b>if({@param enable} == false) : </b>
     * {@param generator} can be null.
     * </pre>
     * @param enable use or not the newly added {@param generator} if any.
     * @param generator who will replace the current one (can be null )
     */
    public void setGenerator(int seed, ProceduralHexGrid generator) {
        if (seed < 0) {
            this.generator = null;
        } else if (generator == null) {
            this.generator = new ProceduralHexGrid(textureKeys.size());
        } else {
            this.generator = generator;
        }
        if (this.generator != null && String.valueOf(seed).length() == 9) {
            this.generator.setSeed(seed);
        } else if (this.generator != null) {
            generateNewSeed();
        }
        updateGeneratorListeners();
    }

    public ProceduralHexGrid getGenerator() {
        return generator;
    }

    /**
     * Generating a new seed cause the map to be reset.
     */
    public void generateNewSeed() {
        Cleanup();
        generator.setSeed(ProceduralHexGrid.generateSeed());
    }

    /**
     * Get tile(s) properties. (one tile)
     *
     * @param tilePos position of the tile.
     * @return can be null
     */
    public HexTile getTile(HexCoordinate tilePos) {
        HexTile t = chunkData.getTile(tilePos);
        if (t == null && generator != null) {
            t = generator.getTileValue(tilePos);
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
            if (result[i] != null && result[i].getTextureKey() == getTextureKey(DefaultTextureValue.NO_TILE)) {
                result[i] = null;
            }
            if (result[i] == null && generator != null) {
                result[i] = generator.getTileValue(tilePos[i]);
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
     * If {@param tileValue} size == 1 the given properties will be apply on all
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
    
    private TileChangeEvent updateTileData(HexCoordinate tilePos, HexTile tile) {
        HexTile oldTile;
        if (tile != null) {
            oldTile = chunkData.add(tilePos,
                    tile.getHeight() >= HexSetting.WATER_LEVEL
                    ? tile : new HexTile(0, getTextureKey(DefaultTextureValue.NO_TILE)));
            if (oldTile != null && generator != null
                    && oldTile.getTextureKey() == getTextureKey(DefaultTextureValue.NO_TILE)) {
                chunkData.remove(tilePos);
                tile = generator.getTileValue(tilePos);
                oldTile = null;
            }
        } else if (chunkData.contain(tilePos)) {
            oldTile = chunkData.remove(tilePos);
        } else if (!chunkData.contain(tilePos) && generator != null) {
            oldTile = chunkData.add(tilePos, new HexTile(0, getTextureKey(DefaultTextureValue.NO_TILE)));
        } else {
            oldTile = null;
        }
        return new TileChangeEvent(tilePos, oldTile, tile);
    }

    /**
     * @todo
     */
    public boolean saveMap(String mapName) {
        this.mapName = mapName;
        return hexGridMapLoader.saveArea(mapName);
    }

    /**
     * @todo
     */
    public boolean loadMap(String mapName) {
        return hexGridMapLoader.loadArea(mapName);
    }
    
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

    public void register(MapDataListener listener) {
        tileListeners.add(listener);
    }

    public void unregister(MapDataListener listener) {
        tileListeners.remove(listener);
    }

    private void updateGeneratorListeners() {
        for (MapDataListener l : tileListeners) {
//            l.onProceduralGenReset(generator);
        }
    }

    /**
     * Call/Update all registered tile listener with the last event.
     *
     * @param tce Last tile event.
     */
    private void updateTileListeners(TileChangeEvent... tce) {
        for (MapDataListener l : tileListeners) {
            l.onTileChange(tce);
        }
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
                return textureKeys.get(textureKey);
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
        int result = textureKeys.indexOf(value);
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
        return Collections.unmodifiableList(textureKeys);
    }

    public enum DefaultTextureValue {

        NO_TILE,
        EMPTY_TEXTURE_KEY,
        SELECTION_TEXTURE
    }

    /**
     * Cleanup the current map.
     */
    public void Cleanup() {
        //Todo remove all file from the temps folder
        chunkData.clear();
    }
}