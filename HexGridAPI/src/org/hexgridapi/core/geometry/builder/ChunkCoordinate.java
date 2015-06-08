package org.hexgridapi.core.geometry.builder;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hexgridapi.core.geometry.builder.coordinate.HexCoordinate;

/**
 * @todo must be a better way for {@link #fromHexCoordinate(java.lang.Class, org.hexgridapi.utility.HexCoordinate)
 * }
 * @
 * author roah
 */
public abstract class ChunkCoordinate {

    private static Class<? extends ChunkCoordinate> coordType;

    static void setCoordType(Class<? extends ChunkCoordinate> chunkCoordinateType) {
        coordType = chunkCoordinateType;
    }

    public static Class<? extends ChunkCoordinate> getBuilderCoordinateType() {
        return coordType;
    }

    public static ChunkCoordinate getNewInstance() {
        if (coordType != null) {
            try {
                return coordType.getConstructor().newInstance();
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(ChunkCoordinate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(ChunkCoordinate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(ChunkCoordinate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ChunkCoordinate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(ChunkCoordinate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(ChunkCoordinate.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        } else {
            throw new ExceptionInInitializerError("There is no coordinate Type to use.");
        }
    }

    public static ChunkCoordinate getNewInstance(Class<?>... clazz) {
        if (coordType != null) {
            try {
                Object[] instances = new Object[clazz.length];
                for (int i = 0; i < clazz.length; i++) {
                    instances[i] = clazz[i].getConstructor().newInstance();
                }
                return coordType.getConstructor(clazz).newInstance(instances);
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(ChunkCoordinate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(ChunkCoordinate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(ChunkCoordinate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ChunkCoordinate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(ChunkCoordinate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(ChunkCoordinate.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        } else {
            throw new ExceptionInInitializerError("There is no coordinate Type to use.");
        }
    }

    public static ChunkCoordinate getNewInstance(Object... param) {
        if (coordType != null) {
            try {
                Class<?>[] paramTypes = new Class<?>[param.length];
                for (int i = 0; i < param.length; i++) {
                    paramTypes[i] = param[i].getClass();
                }
                return coordType.getConstructor(paramTypes).newInstance(param);
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(ChunkCoordinate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(ChunkCoordinate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(ChunkCoordinate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ChunkCoordinate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(ChunkCoordinate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(ChunkCoordinate.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        } else {
            throw new ExceptionInInitializerError("There is no coordinate Type to use.");
        }
    }

    @Override
    public final String toString() {
        return convertToString();
    }

    public abstract ChunkCoordinate add(ChunkCoordinate coord);

    public abstract HexCoordinate getChunkOrigin();

    public abstract boolean containTile(HexCoordinate tile);

    public abstract ChunkCoordinate fromString(String str) throws NumberFormatException;

    public abstract String convertToString();

    @Override
    public final boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        } else {
            return equal((ChunkCoordinate) obj);
        }
    }

    public abstract boolean equal(ChunkCoordinate obj);

    @Override
    public final int hashCode() {
        int hash = 7 + hash();
        return hash;
    }

    protected abstract int hash();
}
