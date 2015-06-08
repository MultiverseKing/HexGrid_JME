package org.hexgridapi.utility;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector2f;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * I don't like to use 2 int or Vector2f for some variable, fully subjective
 * thing nothing more.
 *
 * @author roah
 */
public class Vector2Int implements Savable, Cloneable {
    
    public static final Vector2Int ZERO = new Vector2Int();
    public int x;
    public int y;

    /**
     * X = 0, Y = 0, same as Vector2Int.ZERO.
     */
    public Vector2Int() {
        this.x = 0;
        this.y = 0;
    }
    
    public Vector2Int(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Each value is converted to integer using Math.Floor.
     * @param value
     */
    public Vector2Int(Vector2f value) {
        this((int) value.x, (int) value.y);
    }
    
    /**
     * Math.floor on each param.
     */
    public Vector2Int(float x, float y) {
        this.x = (int) x;
        this.y = (int) y;
    }

    /**
     * return a new Vector2Int as x*i and y*i.
     * @param value multiply factor.
     * @return new Vector2Int
     */
    public Vector2Int multiply(Vector2Int value) {
        return new Vector2Int(this.x * value.x, this.y * value.x);
    }

    /**
     * return a new Vector2Int as 
     * new Vector2Int(this.x + value, this.y + value).
     * @param value value to add
     */
    public Vector2Int add(int value) {
        return new Vector2Int(this.x + value, this.y + value);
    }
    /**
     * return a new Vector2Int as 
     * new Vector2Int(this.x + value.x, this.y + value.y).
     * @param value vector to add
     */
    public Vector2Int add(Vector2Int value) {
        return new Vector2Int(this.x + value.x, this.y + value.y);
    }
    /**
     * return a new Vector2Int as 
     * new Vector2Int(this.x + x, this.y + y).
     * @param value vector to add
     */
    public Vector2Int add(int x, int y) {
        return new Vector2Int(this.x + x, this.y + y);
    }
    
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(this.x, "x", x);
        capsule.write(this.y, "y", y);
    }
    
    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        capsule.readInt("x", this.x);
        capsule.readInt("y", this.y);
    }
    
    /**
     * Convert the vector2Int to string, formated as : x|y.
     *
     * @return string "x|y".
     */
    @Override
    public String toString() {
        return Integer.toString(this.x) + "|" + Integer.toString(this.y);
    }

    /**
     * String must be split as X|Y.
     * @param input
     * @throws NumberFormatException
     */
    public static Vector2Int fromString(String input) throws NumberFormatException {
        String[] strArray = input.split("\\|");
        int x = Integer.parseInt(strArray[0]);
        int y = Integer.parseInt(strArray[1]);
        return new Vector2Int(x, y);
    }

    @Override
    public Vector2Int clone() {
        try {
            return (Vector2Int) super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Vector2Int.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.x;
        hash = 31 * hash + this.y;
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
        final Vector2Int other = (Vector2Int) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }
}
