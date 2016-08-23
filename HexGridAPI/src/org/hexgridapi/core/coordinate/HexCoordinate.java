package org.hexgridapi.core.coordinate;

import org.hexgridapi.core.geometry.HexSetting;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hexgridapi.utility.Rotation;
import org.hexgridapi.utility.Vector2Int;
import org.hexgridapi.utility.Vector3Int;

/**
 * Data is stored as AXIAL cordinate.
 *
 * @author Roah with the help of : ArtemisArt => http://artemis.art.free.fr/ &&
 * http://www.redblobgames.com --Changed version by Eike Foede-- This Class is
 * only used as a converter system so we can simplifie algorithm.
 */
public class HexCoordinate implements Cloneable {

    /**
     * Axial position in Grid. q == x
     */
    private int q;
    /**
     * Axial position in Grid. r == z (or Y)
     */
    private int r;

    private static Vector2Int offsetToAxial(Vector2Int data) {
        return new Vector2Int(data.x - (data.y - (data.y & 1)) / 2, data.y);
    }

    public static Vector3Int offsetToCubic(Vector2Int coord) {
        int x = coord.y - (coord.x - (coord.x & 1)) / 2;
        int z = coord.x;
        int y = -x - z;

        return new Vector3Int(x, y, z);
    }

    /**
     * Only for internal use. (Axial)
     */
    private HexCoordinate(int q, int r) {
        this.q = q;
        this.r = r;
    }

    public HexCoordinate() {
        this(0, 0);
    }

    public HexCoordinate(Coordinate type, Vector2Int pos) {
        if (type.equals(Coordinate.OFFSET)) {
            pos = offsetToAxial(pos);
        } else if (type.equals(Coordinate.CUBIC)) {
            throw new UnsupportedOperationException("Vector2Int is not an allowed Value for Cubic.");
        }
        q = pos.x;
        r = pos.y;
    }

    public HexCoordinate(Coordinate type, int x, int y) {
        this(type, new Vector2Int(x, y));
    }

    /**
     * World Position to Hex grid position. <br>
     * Vector3f to Odd-R Offset grid position.
     * @param pos
     */
    public HexCoordinate(Vector3f pos) {
        float x = pos.x / HexSetting.HEX_WIDTH;
        float z = pos.z + HexSetting.HEX_RADIUS;

        float t1 = z / HexSetting.HEX_RADIUS, t2 = FastMath.floor(x + t1);

        r = (int) FastMath.floor((FastMath.floor(t1 - x) + t2) / 3);
        q = (int) (FastMath.floor((FastMath.floor(2 * x + 1) + t2) / 3) - r);
    }

    /**
     * @param pos CUBIC coordinate
     */
    public HexCoordinate(Vector3Int pos) {
        q = pos.x;
        r = pos.z;
    }

    /**
     * @see <a
     * href="http://www.redblobgames.com/grids/hexagons/#coordinates">redblobgames
     * Coordinate</a>
     * @return
     */
    public Vector3Int toCubic() {
        return new Vector3Int(q, -q - r, r);
    }

    /**
     * “odd-r” horizontal layout is the one used.
     *
     * @see <a
     * href="http://www.redblobgames.com/grids/hexagons/#coordinates">redblobgames
     * Coordinate</a>
     * @return
     */
    public Vector2Int toOffset() {
        return new Vector2Int(q + (r - (r & 1)) / 2, r);
    }

    /**
     * @see <a
     * href="http://www.redblobgames.com/grids/hexagons/#coordinates">redblobgames
     * Coordinate</a>
     * @return
     */
    public Vector2Int toAxial() {
        return new Vector2Int(q, r);
    }

    /**
     * Convert Hex grid position to world position. Convertion work with Odd-R
     * Offset grid type. (currently used grid type).
     * Ignore y value so this.y always = 0
     *
     * @return tile world unit position.
     */
    public Vector3f toWorldPosition() {
        Vector2Int offsetPos = toOffset();
        return new Vector3f((offsetPos.x) * HexSetting.HEX_WIDTH
                + ((offsetPos.y & 1) == 0 ? 0 : HexSetting.HEX_WIDTH / 2), 0.0f, 
                offsetPos.y * HexSetting.HEX_RADIUS * 1.5f);
    }

    /**
     * Convert Hex grid position to world position.
     * Tile height converted to world height.
     *
     * @return tile world unit position.
     */
    public Vector3f toWorldPosition(int height) {
        Vector3f result = toWorldPosition();
        result.y += height * HexSetting.FLOOR_OFFSET;
        return result;
    }

    @Override
    public String toString() {
        return toOffset().x + "|" + toOffset().y;
    }

    public HexCoordinate[] getNeighbours() {
        HexCoordinate[] neighbours = new HexCoordinate[]{
            new HexCoordinate(q + 1, r),
            new HexCoordinate(q + 1, r - 1),
            new HexCoordinate(q, r - 1),
            new HexCoordinate(q - 1, r),
            new HexCoordinate(q - 1, r + 1),
            new HexCoordinate(q, r + 1)
        };
        return neighbours;
    }

    /**
     * Return the distance from this to the provided value.
     *
     * @param other
     * @return hexDistance
     */
    public int distanceTo(HexCoordinate other) {
        return (Math.abs(q - other.q) + Math.abs(r - other.r)
                + Math.abs(q + r - other.q - other.r)) / 2;
    }

    /**
     * Return the rotation to set from this to B.
     * Same as lookAt.
     *
     * @param currentPos
     * @param nextPos
     * @return
     */
    public Rotation getDirection(HexCoordinate targetPos) {
        Vector3Int currentPos = toCubic();
        Vector3Int nextPos = targetPos.toCubic();

        Vector3Int result = new Vector3Int(currentPos.x - nextPos.x, currentPos.y - nextPos.y, currentPos.z - nextPos.z);
        if (result.z == 0 && result.x > 0) {
            return Rotation.D;
        } else if (result.z == 0 && result.x < 0) {
            return Rotation.A;
        } else if (result.y == 0 && result.x > 0) {
            return Rotation.C;
        } else if (result.y == 0 && result.x < 0) {
            return Rotation.F;
        } else if (result.x == 0 && result.y > 0) {
            return Rotation.B;
        } else if (result.x == 0 && result.y < 0) {
            return Rotation.E;
        }
        return null;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof HexCoordinate) {
            final HexCoordinate coord = (HexCoordinate) obj;
            if (coord.toAxial().x == q && coord.toAxial().y == r) {
                return true;
            }
        } else if (obj instanceof Vector2Int) {
            final Vector2Int coord = (Vector2Int) obj;
            if (coord.x == toOffset().x && coord.y == toOffset().y) {
                return true;
            }
        } else if (obj instanceof String) {
            try {
                final HexCoordinate coord = new HexCoordinate(Coordinate.OFFSET, Vector2Int.fromString((String) obj));
                if (coord.toAxial().x == q && coord.toAxial().y == r) {
                    return true;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return q * 2 ^ 16 + r;
    }

    /**
     * Combine two position.
     */
    public HexCoordinate add(HexCoordinate value) {
        return new HexCoordinate(Coordinate.OFFSET, toOffset().add(value.toOffset()));
    }

    /**
     * Combine two position using Vector2Int. (Offset)
     */
    public HexCoordinate add(Vector2Int value) {
        return new HexCoordinate(Coordinate.OFFSET, toOffset().add(value));
    }

    /**
     * Add the value to the position. (Offset)
     */
    public HexCoordinate add(int value) {
        return new HexCoordinate(Coordinate.OFFSET, toOffset().add(value));
    }

    /**
     * Add the value to the position. (Offset)
     */
    public HexCoordinate add(int x, int y) {
        return new HexCoordinate(Coordinate.OFFSET, toOffset().add(x, y));
    }

    /**
     * Return all coordinate between this position and the max range.
     *
     * @return list of coordinate
     */
    public ArrayList<HexCoordinate> getCoordinateInRange(int range) {
        ArrayList<HexCoordinate> result = new ArrayList<HexCoordinate>();
        for (int x = -range; x <= range; x++) {
            for (int y = Math.max(-range, -x - range); y <= Math.min(range, range - x); y++) {
                HexCoordinate coord = new HexCoordinate(Coordinate.AXIAL, new Vector2Int(x + q, y + r));
                if (!coord.toOffset().equals(Vector2Int.ZERO)) {
                    result.add(coord);
                }
            }
        }
        return result;
    }

    /**
     * Generate a ring of coordinate from this position of the desired range.
     *
     * @param range ring position from center.
     * @return all coordinate inside the ring.
     */
    public ArrayList<HexCoordinate> getCoordinateRingAt(int range) {
        ArrayList<HexCoordinate> result = new ArrayList<HexCoordinate>();
        Vector3Int editedOrigin = getNeighbours()[4].toCubic();
        HexCoordinate coord = add(new HexCoordinate(new Vector3Int(
                editedOrigin.x * range, editedOrigin.y * range, editedOrigin.z * range)));

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < range; i++) {
                result.add(coord);
                coord = coord.getNeighbours()[i];
            }
        }
        return result;
    }

    /**
     * Chexk the range using Offset coordinate.
     *
     * @param offsetPos Offset coordinate of the tile.
     * @param range the max range.
     * @return true if the position is in the range
     */
    public boolean hasInRange(Vector2Int offsetPos, int range) {
        return hasInRange(new HexCoordinate(Coordinate.OFFSET, offsetPos), range);
    }

    public boolean hasInRange(HexCoordinate pos, int range) {
        if (this.equals(pos)) {
            return true;
        }
        for (int x = -range; x <= range; x++) {
            for (int y = Math.max(-range, -x - range); y <= Math.min(range, range - x); y++) {
                if (q + x == pos.q && r + y == pos.r) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public HexCoordinate clone() {
        try {
            return (HexCoordinate) super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(HexCoordinate.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public enum Coordinate {

        /**
         * Offset R coordinate.
         */
        OFFSET,
        /**
         * Axial coordinate.
         */
        AXIAL,
        /**
         * Cubic coordinate.
         */
        CUBIC;
    }
}
