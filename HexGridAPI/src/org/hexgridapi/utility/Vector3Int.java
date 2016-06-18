package org.hexgridapi.utility;

import com.jme3.math.Vector3f;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * We don't like to use 3 int or Vector3f for some variable, fully subjective
 * thing, nothing more.
 *
 * @author roah, Eike Foede
 */
public class Vector3Int implements Cloneable {

    public static final Vector3Int ZERO = new Vector3Int();
    public int x;
    public int y;
    public int z;

    public Vector3Int() {
        this(0, 0, 0);
    }

    public Vector3Int(Vector3f value) {
        this((int) value.x, (int) value.y, (int) value.z);
    }

    public Vector3Int(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Add the provided value to this as :
     * this.x + {@param value.x}, this.y + {@param value.y}, this.z + {@param value.z}.
     * 
     * @return newly generated vector.
     */
    public Vector3Int add(Vector3Int value) {
        return new Vector3Int(x + value.x, y + value.y, z + value.z);
    }

    /**
     * Add the provided value to this as :
     * this.x + {@param x}, this.y + {@param y}, this.z + {@param z}.
     * 
     * @return newly generated vector.
     */
    public Vector3Int add(int x, int y, int z) {
        return new Vector3Int(this.x + x, this.y + y, this.z + z);
    }

    /**
     * return a new Vector2Int as x*{@param i} and y*{@param i}.
     *
     * @param i multiply factor.
     * @return new Vector2Int
     */
    public Vector3Int multiply(int i) {
        return new Vector3Int(this.x * i, this.y * i, this.z * i);
    }

    /**
     * Convert the vector2Int to string, formated as : x|y|z.
     *
     * @return string "x|y|z".
     */
    @Override
    public String toString() {
        return Integer.toString(this.x) + "|" + Integer.toString(this.y) + "|" + Integer.toString(this.z);
    }

    /**
     * String must be split as X|Y|Z.
     *
     * @param input
     * @throws NumberFormatException
     */
    public static Vector3Int fromString(String input) throws NumberFormatException {
        String[] strArray = input.split("\\|");
        int x = Integer.parseInt(strArray[0]);
        int y = Integer.parseInt(strArray[1]);
        int z = Integer.parseInt(strArray[2]);
        return new Vector3Int(x, y, z);
    }

    @Override
    protected Vector3Int clone() {
        try {
            return (Vector3Int) super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Vector3Int.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.x;
        hash = 97 * hash + this.y;
        hash = 97 * hash + this.z;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vector3Int other = (Vector3Int) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        if (this.z != other.z) {
            return false;
        }
        return true;
    }
}
