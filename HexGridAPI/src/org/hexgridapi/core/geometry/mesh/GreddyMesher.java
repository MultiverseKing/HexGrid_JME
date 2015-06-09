package org.hexgridapi.core.geometry.mesh;

import com.jme3.scene.Mesh;
import java.util.HashMap;
import java.util.Iterator;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.data.HexTile;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.geometry.builder.ChunkCoordinate;
import org.hexgridapi.core.geometry.builder.coordinate.HexagonCoordinate;
import org.hexgridapi.core.geometry.builder.coordinate.SquareCoordinate;
import org.hexgridapi.core.geometry.builder.coordinate.HexCoordinate;
import org.hexgridapi.core.geometry.builder.coordinate.HexCoordinate.Coordinate;
import org.hexgridapi.utility.Vector2Int;

/**
 * Used to generate the needed data to generate all
 * mesh contained inside a chunk, mesh are split by Height.
 * /!\ Also mesh can be split by texture if not using texture Array.
 *
 * @author roah
 */
public final class GreddyMesher {

    private final MapData mapData;
    /**
     * List containing all the chunk data.
     */
    private HashMap<String, MesherData> meshData = new HashMap<String, MesherData>();
    /**
     * The Depth
     *
     * @see {@link org.hexgridapi.core.HexSetting#CHUNK_DEPTH}
     */
    private int groundHeight = HexSetting.CHUNK_DEPTH;
    private boolean onlyGround;
    private String inspectedTexture;
    private ChunkCoordinate chunkCoordinate;
    private final boolean useArrayTexture;
    private Iterator<String> meshIterator;
    private boolean onlyVoid;

    public GreddyMesher(MapData mapData, boolean useArrayTexture) {
        this.mapData = mapData;
        this.useArrayTexture = useArrayTexture;
    }

    private void initialize(boolean onlyGround, boolean onlyVoid) {
        clear();
        this.onlyGround = onlyGround;
        this.onlyVoid = onlyVoid;
        boolean[][] isVisited = getVisitedList();
        /**
         * x && y == coord local
         */
        for (int y = 0; y < isVisited.length; y++) {
            for (int x = 0; x < isVisited.length; x++) {
                if (!isVisited[x][y]) {
                    HexTile currentTile;
                    String textValue;
                    boolean currentIsInRange;
                    if (isHexagon()) {
                        currentIsInRange = getIsInRange(null, x, y);
                        currentTile = currentIsInRange && !onlyVoid ? mapData.getTile(getNextTileCoord(null, x, y)) : null;
                    } else {
//                        Vector2Int chunkInitTile = new HexCoordinate(Coordinate.CHUNK, chunkCoordinate).toOffset();
                        currentTile = !onlyVoid ? mapData.getTile(new HexCoordinate(Coordinate.OFFSET,
                                x + chunkCoordinate.getChunkOrigin().toOffset().x,
                                y + chunkCoordinate.getChunkOrigin().toOffset().y)) : null;
                        currentIsInRange = false;
                    }
                    if (isHexagon() && !currentIsInRange) {
                        textValue = null;
                    } else if (currentTile == null) {
                        textValue = MapData.DefaultTextureValue.NO_TILE.toString();
                    } else {
                        textValue = mapData.getTextureValue(currentTile.getTextureKey());
                    }
                    Integer tileHeight = currentTile == null ? null : currentTile.getHeight();

                    if (meshData.isEmpty() || !meshData.containsKey(textValue)) {
                        meshData.put(textValue, new MesherData(new Vector2Int(x, y), tileHeight));
                    } else {
                        meshData.get(textValue).add(new Vector2Int(x, y), tileHeight);
                    }

                    if (tileHeight != null && tileHeight < groundHeight + HexSetting.CHUNK_DEPTH) {
                        groundHeight = tileHeight;
                    }

                    setSize(isVisited, meshData.get(textValue), currentTile, currentIsInRange, onlyVoid);
                }
            }
        }
    }

    private void setSize(boolean[][] isVisited, MesherData currentGreddyData,
            HexTile currentTile, boolean currentIsInRange, boolean onlyVoid) {

        // Define the size on X.
        for (int x = 1; x < isVisited.length - currentGreddyData.getLastAddedPosition().x; x++) {
            boolean alreadyVisited = isVisited[currentGreddyData.getLastAddedPosition().x + x][currentGreddyData.getLastAddedPosition().y];
            HexTile nextTile;
            boolean nextIsInRange = isHexagon()
                    ? getIsInRange(currentGreddyData.getLastAddedPosition(), x, 0) : false;
            if (!onlyVoid && (isSquare() || nextIsInRange)) {
                nextTile = mapData.getTile(getNextTileCoord(currentGreddyData.getLastAddedPosition(), x, 0));
            } else {
                nextTile = null;
            }
            if (isValidToExpand(alreadyVisited, currentTile, nextTile, currentIsInRange, nextIsInRange)) {
                currentGreddyData.expandSizeX();
                isVisited[currentGreddyData.getLastAddedPosition().x + x][currentGreddyData.getLastAddedPosition().y] = true;
            } else {
                break;
            }
        }
        // Define the size on Y.
        for (int y = 1; y < isVisited.length - currentGreddyData.getLastAddedPosition().y; y++) {
            //We check if the next Y line got the same properties
            for (int x = 0; x < currentGreddyData.getLastAddedSize().x; x++) {
                boolean alreadyVisited = isVisited[currentGreddyData.getLastAddedPosition().x + x][currentGreddyData.getLastAddedPosition().y + y];
//                HexTile nextTile = mapData.getTile(getNextTileCoord(radius, currentGreddyData.getLastAddedPosition(), x, y));

                HexTile nextTile;
                boolean nextIsInRange = isHexagon()
                        ? getIsInRange(currentGreddyData.getLastAddedPosition(), x, y) : false;
                if (!onlyVoid && (isSquare() || nextIsInRange)) {
                    nextTile = mapData.getTile(getNextTileCoord(currentGreddyData.getLastAddedPosition(), x, y));
                } else {
                    nextTile = null;
                }
                if (!isValidToExpand(alreadyVisited, currentTile, nextTile, nextIsInRange, currentIsInRange)) {
                    //if one tile didn't match the requirement we stop the search
                    return;
                }
            }
            //all tile meet the requirement we increase the size Y
            currentGreddyData.expandSizeY();
            //we set that line as visited so we don't do any operation later for them
            Vector2Int lastAddedPosition = currentGreddyData.getLastAddedPosition();
            for (int x = 0; x < currentGreddyData.getLastAddedSize().x; x++) {
                isVisited[lastAddedPosition.x + x][lastAddedPosition.y + y] = true;
            }
        }
    }

    private boolean isValidToExpand(boolean alreadyVisited, HexTile currentTile,
            HexTile nextTile, boolean nextIsInRange, boolean currentIsInRange) {
        if (!alreadyVisited) {
            if (isSquare()) {
                if (currentTile == null && nextTile == null) {
                    return true;
                } else if (currentTile != null && nextTile != null
                        && hasSameTexture(currentTile, nextTile)
                        && hasSameHeight(currentTile, nextTile)) {
                    return true;
                }
            } else if (isHexagon()) {
                if (nextIsInRange == currentIsInRange) {
                    if (currentTile == null && nextTile == null) {
                        return true;
                    } else if (currentTile != null && nextTile != null
                            && hasSameTexture(currentTile, nextTile)
                            && hasSameHeight(currentTile, nextTile)) {
                        return true;
                    }
                }
            } else {
                throw new UnsupportedOperationException(ChunkCoordinate.getBuilderCoordinateType()
                        + " is not currently a supported type");
            }
        }
        return false;
    }

    private boolean isHexagon() {
        return ChunkCoordinate.getBuilderCoordinateType().isAssignableFrom(HexagonCoordinate.class);
    }

    private boolean isSquare() {
        return ChunkCoordinate.getBuilderCoordinateType().isAssignableFrom(SquareCoordinate.class);
    }

    private boolean hasSameHeight(HexTile currentTile, HexTile nextTile) {
        if (currentTile.getHeight() == nextTile.getHeight()) {
            return true;
        }
        return false;
    }

    private boolean hasSameTexture(HexTile currentTile, HexTile nextTile) {
        if (currentTile != null && nextTile != null) {
            if (currentTile.getTextureKey() == nextTile.getTextureKey()) {
                return true;
            }
        }
        return false;
    }

    private boolean getIsInRange(Vector2Int position, int x, int y) {
        return new HexCoordinate(Coordinate.OFFSET, HexSetting.CHUNK_SIZE, HexSetting.CHUNK_SIZE)
                .hasInRange(new HexCoordinate(Coordinate.OFFSET,
                (position != null ? position.x : 0) + x,
                (position != null ? position.y : 0) + y), HexSetting.CHUNK_SIZE);
    }

    /**
     * @return tile coordinate inside mapData.
     */
    private HexCoordinate getNextTileCoord(Vector2Int inspectedPos, int x, int y) {
        if (isHexagon()) {
            Vector2Int coord = new Vector2Int(
                    x + (inspectedPos != null ? inspectedPos.x : 0) - HexSetting.CHUNK_SIZE,
                    y + (inspectedPos != null ? inspectedPos.y : 0) - HexSetting.CHUNK_SIZE);
            //-----------------------------
            if ((HexSetting.CHUNK_SIZE & 1) == 0 && (chunkCoordinate.getChunkOrigin().toOffset().y & 1) != 0 && (coord.y & 1) != 0) {
                return chunkCoordinate.getChunkOrigin().add(coord.x + 1, coord.y);
            } else if ((HexSetting.CHUNK_SIZE & 1) != 0 && (chunkCoordinate.getChunkOrigin().toOffset().y & 1) == 0 && (coord.y & 1) != 0) {
                return chunkCoordinate.getChunkOrigin().add(coord.x - 1, coord.y);
            } else {
                return chunkCoordinate.getChunkOrigin().add(coord);
            }
        } else {
            return chunkCoordinate.getChunkOrigin().add(x + inspectedPos.x, y + inspectedPos.y);
        }
    }

    private boolean[][] getVisitedList() {
        int chunkSize = isSquare() ? HexSetting.CHUNK_SIZE : (HexSetting.CHUNK_SIZE * 2) + 1;
        boolean[][] isVisited = new boolean[chunkSize][chunkSize];
        return isVisited;
    }

    // <editor-fold defaultstate="collapsed" desc="Getters">
    public boolean useArrayTexture() {
        return useArrayTexture;
    }

    /**
     * Generate a mesh being containing only void Tile.
     *
     * @param onlyGround generate side face ?
     * @return
     */
    public Mesh getMesh(boolean onlyGround, boolean onlyvoid) {
//        this.chunkCoordinate = inspectedChunk;
        initialize(onlyGround, onlyvoid);
        return generateMesh(true).get("NO_TILE");
    }

    /**
     * Generate one or multiple mesh corresponding to the data contained in
     * mapData if not ghost with the setting of HexSetting,
     * the count of generated mesh is equals the amount of texture (if not using
     * texture Array).
     *
     * @param onlyGround generate side face ?
     * @return list of all generated mesh. (1 mesh by texture if no
     * arrayTexture)
     */
    public HashMap<String, Mesh> getMesh(boolean onlyGround, boolean generateVoid, ChunkCoordinate inspectedChunk) {
        this.chunkCoordinate = inspectedChunk;
        initialize(onlyGround, false);
        return generateMesh(generateVoid);
    }

    private HashMap<String, Mesh> generateMesh(boolean generateVoid) {
        if (!generateVoid) {
            meshData.remove(MapData.DefaultTextureValue.NO_TILE.toString());
        }
        if (isHexagon()) {
            meshData.remove(null);
        }
        HashMap<String, Mesh> mesh = new HashMap<String, Mesh>(meshData.size());
        meshIterator = meshData.keySet().iterator();

        if (useArrayTexture) {
            String value = meshIterator.next();
            inspectedTexture = value; // Send this to the generator
            mesh.put("ArrayTextureMesh", MeshGenerator.getInstance().getMesh(this));
        } else {
            while (meshIterator.hasNext()) {
                String value = meshIterator.next();
                inspectedTexture = value; // Send this to the generator
                mesh.put(value, MeshGenerator.getInstance().getMesh(this));
            }
        }
        return mesh;
    }

    /**
     * @return position in chunk of the current element mesh visited.
     */
    public Vector2Int getCurrentPositionParam() {
        return meshData.get(inspectedTexture).getPosition();
    }

    /**
     * @return the size of the current element mesh visited.
     */
    public Vector2Int getCurrentSizeParam() {
        return meshData.get(inspectedTexture).getSize();
    }

    /**
     * @return height of the current element mesh visited.
     */
    public int getCurrentHeightParam() {
        return meshData.get(inspectedTexture).getHeight();
    }

    /**
     * @return height of the current element mesh visited.
     */
    public int getCurrentTextureIDParam() {
        if (inspectedTexture.equals(MapData.DefaultTextureValue.NO_TILE.toString())
                || inspectedTexture.equals(MapData.DefaultTextureValue.EMPTY_TEXTURE_KEY.toString())) {
            return 0;
        }
        return mapData.getTextureKey(inspectedTexture);
    }

    /**
     * set to true if the depth isn't needed.
     *
     * @return
     */
    public boolean onlyGround() {
        return onlyGround;
    }

    public int getGroundHeight() {
        return groundHeight + HexSetting.CHUNK_DEPTH;
    }

    /**
     * How many mesh param this element have.
     *
     * @return
     */
    public int getElementMeshCount() {
        return meshData.get(inspectedTexture).size();
    }

    /**
     * Return true if there is another mesh to generate for the current element.
     *
     * @return
     */
    public boolean hasNext() {
        boolean hasNext = meshData.get(inspectedTexture).hasNext();
        if (useArrayTexture) {
            if (!hasNext && meshIterator.hasNext()) {
                inspectedTexture = meshIterator.next();
                return meshData.get(inspectedTexture).hasNext();
            }
        }
        return hasNext;
    }
    // </editor-fold>

    /**
     * Culling data corresponding to the currently inspected Mesh
     *
     * @return
     */
    public CullingData getCullingData() {
        return new CullingData();


    }

    /**
     * Internal use.
     *
     * @todo Since HexGridAPI_v.1.1.9.preAlpha the culling on chunk edge is
     * always set to false, this only serve to avoid calculation when editing
     * the grid once generated, there is no use of it if the grid isn't mean
     * to be edited once generated. An improvement is needed to let the user
     * chose if it will edit or not the grid once generated so we know if this
     * have to be enabled or not.
     */
    public class CullingData {

        //0 == top; 1 == bot; 2 = left; 3 = right;
        //if culling == true do not show the face.
        boolean[][][] culling = new boolean[4][][];

        private CullingData() {
            MesherData inspectedMesh = meshData.get(inspectedTexture);
            boolean isOddStart = (inspectedMesh.getPosition().y & 1) == 0;

            HexCoordinate coord;
            if(!onlyVoid) {
                Vector2Int chunkInitTile = chunkCoordinate.getChunkOrigin().toOffset();
                coord = new HexCoordinate(Coordinate.OFFSET,
                        inspectedMesh.getPosition().x + chunkInitTile.x, inspectedMesh.getPosition().y + chunkInitTile.y);
            } else {
                coord = null;
            }
            HexTile[] neightbors = !onlyVoid ? new HexTile[6] : new HexTile[]{null, null, null, null, null, null};
            for (int i = 0; i < 4; i++) {
                int currentSize = (i == 0 || i == 1 ? inspectedMesh.getSize().x : inspectedMesh.getSize().y);
                culling[i] = new boolean[currentSize][3];
                for (int j = 0; j < currentSize; j++) {

                    if (i == 0) { // top chunk = -(Z)
                        if (isSquare() && inspectedMesh.getPosition().y == 0) {
                            culling[i][j][0] = false; // top left
                            culling[i][j][1] = false; // top right
                            culling[i][j][2] = false;
                        } else {
                            neightbors = !onlyVoid ? mapData.getNeightbors(coord.add(j, 0)) : neightbors;
                            culling[i][j][0] = neightbors[2] == null || neightbors[2].getHeight() < inspectedMesh.getHeight() ? false : true; // top left
                            culling[i][j][1] = neightbors[1] == null || neightbors[1].getHeight() < inspectedMesh.getHeight() ? false : true; // top right
                            culling[i][j][2] = false;
                        }
                    } else if (i == 1) { //bot chunk = (Z)
                        if (isSquare() && inspectedMesh.getPosition().y == HexSetting.CHUNK_SIZE - 1) {
                            culling[i][j][0] = false; // top left
                            culling[i][j][1] = false; // top right
                            culling[i][j][2] = false;
                        } else {
                            neightbors = !onlyVoid ? mapData.getNeightbors(coord.add(j, inspectedMesh.getSize().y - 1)) : neightbors;
                            culling[i][j][0] = neightbors[4] == null || neightbors[4].getHeight() < inspectedMesh.getHeight() ? false : true; // bot left
                            culling[i][j][1] = neightbors[5] == null || neightbors[5].getHeight() < inspectedMesh.getHeight() ? false : true; // bot right
                            culling[i][j][2] = false;
                        }
                    } else if (i == 2) { // left chunk = -(X)
                        if (isSquare() && inspectedMesh.getPosition().x == 0) {
                            culling[i][j][0] = false; // top left
                            culling[i][j][1] = false; // top right
                            culling[i][j][2] = false;
                        } else {
                            neightbors = !onlyVoid ? mapData.getNeightbors(coord.add(0, j)) : neightbors;
                            culling[i][j][0] = neightbors[3] == null || neightbors[3].getHeight() < inspectedMesh.getHeight() ? false : true; // left
                            if (isOddStart && (j & 1) == 0) {
                                culling[i][j][1] = j != 0 && neightbors[2] == null || j != 0 && neightbors[2].getHeight() < inspectedMesh.getHeight() ? false : true; // top left
                                culling[i][j][2] = j != currentSize - 1 && neightbors[4] == null || j != currentSize - 1 && neightbors[4].getHeight() < inspectedMesh.getHeight() ? false : true; // bot left
                            } else if (!isOddStart && (j & 1) != 0) {
                                culling[i][j][1] = neightbors[2] == null || neightbors[2].getHeight() < inspectedMesh.getHeight() ? false : true; // top left
                                culling[i][j][2] = j != currentSize - 1 && neightbors[4] == null || j != currentSize - 1 && neightbors[4].getHeight() < inspectedMesh.getHeight() ? false : true; // bot left
                            } else {
                                culling[i][j][1] = false; // top left ignored
                                culling[i][j][2] = false; // bot left ignored
                            }
                        }
                    } else { // right chunk = (X)
                        if (isSquare() && inspectedMesh.getPosition().x == HexSetting.CHUNK_SIZE - 1) {
                            culling[i][j][0] = false; // top left
                            culling[i][j][1] = false; // top right
                            culling[i][j][2] = false;
                        } else {
                            neightbors = !onlyVoid ? mapData.getNeightbors(coord.add(inspectedMesh.getSize().x - 1, j)) : neightbors;
                            culling[i][j][0] = neightbors[0] == null || neightbors[0].getHeight() < inspectedMesh.getHeight() ? false : true; // right
                            if (!isOddStart && (j & 1) == 0) {
                                culling[i][j][1] = j != 0 && neightbors[1] == null || j != 0 && neightbors[1].getHeight() < inspectedMesh.getHeight() ? false : true; // top right
                                culling[i][j][2] = j != currentSize - 1 && neightbors[5] == null || j != currentSize - 1 && neightbors[5].getHeight() < inspectedMesh.getHeight() ? false : true; // bot right
                            } else if (isOddStart && (j & 1) != 0) {
                                culling[i][j][1] = neightbors[1] == null || neightbors[1].getHeight() < inspectedMesh.getHeight() ? false : true; // top right
                                culling[i][j][2] = j != currentSize - 1 && neightbors[5] == null || j != currentSize - 1 && neightbors[5].getHeight() < inspectedMesh.getHeight() ? false : true; // bot right
                            } else {
                                culling[i][j][1] = false; // top right ignored
                                culling[i][j][2] = false; // bot right ignored
                            }
                        }
                    }

                }
            }

        }

        /**
         * return the culling on the desired location.
         *
         * @param pos of the inspected side.
         * @param tilePosition position of the til inside the chunk.
         * @param facePos needed face of the selected tile.
         * @return true if the face have to be culled.
         */
        public boolean getCulling(Position pos, int tilePosition, Position facePos) {
            int index;
            if (pos.equals(Position.TOP)) {
                if (facePos.equals(Position.TOP_LEFT)) {
                    index = 0;
                } else if (facePos.equals(Position.TOP_RIGHT)) {
                    index = 1;
                } else {
                    throw new UnsupportedOperationException(facePos + " is not allowed in the current context.");
                }
            } else if (pos.equals(Position.BOTTOM)) {
                if (facePos.equals(Position.BOT_LEFT)) {
                    index = 0;
                } else if (facePos.equals(Position.BOT_RIGHT)) {
                    index = 1;
                } else {
                    throw new UnsupportedOperationException(facePos + " is not allowed in the current context.");
                }
            } else if (pos.equals(Position.LEFT)) {
                if (facePos.equals(Position.LEFT)) {
                    index = 0;
                } else if (facePos.equals(Position.TOP_LEFT)) {
                    index = 1;
                } else if (facePos.equals(Position.BOT_LEFT)) {
                    index = 2;
                } else {
                    throw new UnsupportedOperationException(facePos + " is not allowed in the current context.");
                }
            } else if (pos.equals(Position.RIGHT)) {
                if (facePos.equals(Position.RIGHT)) {
                    index = 0;
                } else if (facePos.equals(Position.TOP_RIGHT)) {
                    index = 1;
                } else if (facePos.equals(Position.BOT_RIGHT)) {
                    index = 2;
                } else {
                    throw new UnsupportedOperationException(facePos + " is not allowed in the current context.");
                }
            } else {
                throw new UnsupportedOperationException(pos + " is not allowed in the current context.");
            }
            return culling[pos.ordinal()][tilePosition][index];
        }
    }

    public enum Position {

        TOP,
        BOTTOM,
        LEFT,
        RIGHT,
        TOP_LEFT,
        TOP_RIGHT,
        BOT_LEFT,
        BOT_RIGHT;
    }

    private void clear() {
        meshData.clear();
        groundHeight = 0;
    }
}
