package hexmap;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import java.util.List;
import org.hexgridapi.core.appstate.AbstractHexGridAppState;
import org.hexgridapi.core.appstate.MouseControlSystem;
import org.hexgridapi.core.control.TileSelectionControl;
import org.hexgridapi.core.data.HexTile;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.geometry.builder.coordinate.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;

/**
 *
 * @author roah
 */
public class HexMapSystem extends AbstractHexGridAppState {

    private TileSelectionControl selectionControl;
    private MapData mapData;

    HexMapSystem(MapData mapData) {
        super(mapData);
        this.mapData = mapData;
    }

    @Override
    public void initializeSystem(AppStateManager stateManager, Application app) {
        selectionControl = app.getStateManager().getState(MouseControlSystem.class).getSelectionControl();
        if (mapData.getGenerator() == null && !builder.useBuffer()) {
            //<editor-fold defaultstate="collapsed" desc="Initialise some tile to show something">
            mapData.setTile(new HexCoordinate[]{
                // H letters //
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(1, 0)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(2, 0)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(3, 0)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(4, 0)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(5, 0)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(6, 0)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(1, 4)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(2, 4)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(3, 4)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(4, 4)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(5, 4)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(6, 4)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(3, 1)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(3, 2)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(3, 3)),
                // E letters //
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(1, -2)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(2, -2)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(3, -2)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(4, -2)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(5, -2)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(6, -2)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(1, -3)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(1, -4)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(1, -5)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(1, -6)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(3, -3)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(3, -4)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(3, -5)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(6, -3)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(6, -4)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(6, -5)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(6, -6)),
                // L letters //
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(1, -8)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(2, -8)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(3, -8)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(4, -8)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(5, -8)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(6, -8)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(6, -9)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(6, -10)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(6, -11)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(6, -12)),
                // L letters //
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(1, -14)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(2, -14)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(3, -14)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(4, -14)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(5, -14)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(6, -14)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(6, -15)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(6, -16)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(6, -17)),
                new HexCoordinate(HexCoordinate.Coordinate.OFFSET, new Vector2Int(6, -18)),
            }, new HexTile[]{new HexTile()});
            // </editor-fold>
        }
    }

    @Override
    public void updateSystem(float tpf) {
    }

    @Override
    public void cleanupSystem() {
        hexGridAPINode.removeFromParent();
    }

    // <editor-fold defaultstate="collapsed" desc="Getters && Setters">
    public boolean containChunk() {
        if (builder.isEmpty()) {
            //@todo
        }
        return true;
    }

    /**
     * The seed currently used to generate the grid.
     *
     * @return
     */
    public int getSeed() {
        return mapData.getSeed();
    }

    public boolean isUsingProcedural() {
        return builder.useProcedural();
    }

    /**
     * Hide/Show all tile who contain a null pointer.
     *
     * @param setVisible
     */
    public void hideGhost(boolean setVisible) {
        builder.hideGhostTile(setVisible);
    }

    /**
     * Set the current map name.
     *
     * @param name to set
     */
    public void setMapName(String name) {
        mapData.setMapName(name);
    }

    /**
     * @return The current map name.
     */
    public String getMapName() {
        return mapData.getMapName();
    }

    /**
     * Delete the currently selected tile.
     */
    public void removeTile() {
        if (selectionControl.getSelectedList().isEmpty()) {
            mapData.setTile(selectionControl.getSelectedPos(), null);
        } else {
            mapData.setTile(selectionControl.getSelectedList().toArray(
                    new HexCoordinate[selectionControl.getSelectedList().size()]),
                    new HexTile[]{null});
        }
    }

    /**
     * Set the currently selected tile to default.
     */
    public void setNewTile() {
        if (selectionControl.getSelectedList().isEmpty()) {
            HexTile t = getTile();
            if (t != null) {
                t.cloneChangedTextureKey(mapData.getTextureKey(MapData.DefaultTextureValue.EMPTY_TEXTURE_KEY));
            } else {
                t = new HexTile();
            }
            mapData.setTile(selectionControl.getSelectedPos(), t);
        } else {
            mapData.setTile(selectionControl.getSelectedList().toArray(
                    new HexCoordinate[selectionControl.getSelectedList().size()]),
                    new HexTile[]{new HexTile()});
        }
    }

    /**
     * set the currently selected tile to the specifiated texture.
     *
     * @param textureKey texture to set.
     */
    public void setTilePropertiesTexTure(String textureKey) {
        if (!selectionControl.getSelectedList().isEmpty()) {
            HexCoordinate[] tileList = selectionControl.getSelectedList().toArray(
                    new HexCoordinate[selectionControl.getSelectedList().size()]);
            HexTile[] t = mapData.getTile(tileList);
            for (int i = 0; i < t.length; i++) {
                if (t[i] != null) {
                    t[i] = t[i].cloneChangedTextureKey(mapData.getTextureKey(textureKey));
                } else {
                    t[i] = new HexTile(0, mapData.getTextureKey(textureKey));
                }
            }
            mapData.setTile(tileList, t);
        } else {
            HexTile t = mapData.getTile(selectionControl.getSelectedPos());
            if (t != null) {
                t = t.cloneChangedTextureKey(mapData.getTextureKey(textureKey));
            } else {
                t = new HexTile(0, mapData.getTextureKey(textureKey));
            }
            mapData.setTile(selectionControl.getSelectedPos(), t);
        }
    }

    /**
     * Set the currently selected tile to the specifiated height.
     *
     * @param height
     */
    public void setTilePropertiesHeight(int height) {
        mapData.setTile(selectionControl.getSelectedPos(),
                mapData.getTile(selectionControl.getSelectedPos()).cloneChangedHeight(height));
    }

    /**
     * Increase the height of the currently selected tile from one.
     */
    public void setTilePropertiesUp() {
        if (!selectionControl.getSelectedList().isEmpty()) {
            HexCoordinate[] tileList = selectionControl.getSelectedList().toArray(
                    new HexCoordinate[selectionControl.getSelectedList().size()]);
            HexTile[] t = mapData.getTile(tileList);
            for (int i = 0; i < t.length; i++) {
                if (t[i] != null) {
                    t[i] = t[i].cloneChangedHeight(t[i].getHeight() + 1);
                } else {
                    t[i] = new HexTile(1);
                }
            }
            mapData.setTile(tileList, t);
        } else {
            HexTile t = mapData.getTile(selectionControl.getSelectedPos());
            if (t != null) {
                t = t.cloneChangedHeight(t.getHeight() + 1);
            } else {
                t = new HexTile(1);
            }
            mapData.setTile(selectionControl.getSelectedPos(), t);
        }
    }

    /**
     * Decrease the height of the currently selected tile from one.
     */
    public void setTilePropertiesDown() {
        if (!selectionControl.getSelectedList().isEmpty()) {
            HexCoordinate[] tileList = selectionControl.getSelectedList().toArray(
                    new HexCoordinate[selectionControl.getSelectedList().size()]);
            HexTile[] t = mapData.getTile(tileList);
            for (int i = 0; i < t.length; i++) {
                if (t[i] != null) {
                    t[i] = t[i].cloneChangedHeight((t[i].getHeight() - 1));
                } else {
                    t[i] = new HexTile(-1);
                }
            }
            mapData.setTile(tileList, t);
        } else {
            HexTile t = mapData.getTile(selectionControl.getSelectedPos());
            if (t != null) {
                t = t.cloneChangedHeight(t.getHeight() - 1);
            } else {
                t = new HexTile(-1);
            }
            mapData.setTile(selectionControl.getSelectedPos(), t);
        }
    }

    /**
     *
     * @return true if the currently selected tile contain a value other than
     * null.
     */
    public boolean tileExist() {
        HexTile t = getTile();
        if (t != null) {
            return mapData.getTextureValue(t.getTextureKey()).equals(MapData.DefaultTextureValue.NO_TILE.toString()) ? false : true;
        } else {
            return false;
        }
    }

    /**
     * @return the currently selected tile.
     */
    public HexTile getTile() {
        return mapData.getTile(selectionControl.getSelectedPos());
    }

    /**
     * @return the height of the currently selected tile.
     */
    public int getTileHeight() {
        HexTile tile = mapData.getTile(selectionControl.getSelectedPos());
        if (tile != null) {
            return tile.getHeight();
        } else {
            return 0;
        }
    }

    /**
     * @return the texture value of the currently selected tile.
     */
    public int getTileTextureValue() {
        HexTile tile = mapData.getTile(selectionControl.getSelectedPos());
        if (tile != null) {
            return tile.getTextureKey();
        } else {
            return 0;
        }
    }

    /**
     * @return the texture Key of the currently selected tile.
     */
    public String getTileTextureKey() {
        HexTile tile = mapData.getTile(selectionControl.getSelectedPos());
        if (tile != null) {
            return mapData.getTextureValue(tile.getTextureKey());
        } else {
            return mapData.getTextureValue(0);
        }
    }

    /**
     * @return all texture key currently set.
     */
    public List<String> getTextureKeys() {
        return mapData.getTextureKeys();
    }

    /**
     * @return the default texture used when creating a tile.
     */
    public String getTextureDefault() {
        return mapData.getTextureKeys().get(0);
    }

    /**
     * Texture name set when initialising them.
     *
     * @param textureKey needed key
     * @return name of the texture corresponding to hat key.
     */
    public String getTextureValueFromKey(int textureKey) {
        return mapData.getTextureValue(textureKey);
    }
    // </editor-fold>
}
