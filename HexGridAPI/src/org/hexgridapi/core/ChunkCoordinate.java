package org.hexgridapi.core;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hexgridapi.core.coordinate.BufferBuilder;
import org.hexgridapi.core.coordinate.HexCoordinate;

/**
 * @todo must be a better way for {@link #fromHexCoordinate(java.lang.Class, org.hexgridapi.utility.HexCoordinate)
 * }
 * @
 * author roah
 */
public abstract class ChunkCoordinate implements BufferBuilder {

    private static Class<? extends ChunkCoordinate> coordType;
    protected static int chunkSize;

    static void setCoordType(Class<? extends ChunkCoordinate> chunkCoordinateType, int chunkSize_) {
        coordType = chunkCoordinateType;
        chunkSize = chunkSize_;
//        if(coordType == null) {
//        } else {
//            Logger.getLogger(ChunkCoordinate.class.getName()).log(Level.WARNING, 
//                    "Coordinate already defined as : {0}", new Object[]{coordType.getName()});
//        }
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

    public static int getChunkSize() {
        return chunkSize;
    }

    @Override
    public final String toString() {
        return convertToString();
    }

    public abstract ChunkCoordinate add(ChunkCoordinate coord);

    public abstract HexCoordinate getChunkOrigin();
    
    public abstract HexCoordinate getChunkCenter();

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
