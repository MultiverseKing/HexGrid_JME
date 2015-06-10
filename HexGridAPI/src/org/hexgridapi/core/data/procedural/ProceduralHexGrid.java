package org.hexgridapi.core.data.procedural;

import com.jme3.math.FastMath;
import java.util.logging.Level;
import java.util.logging.Logger;
import libnoiseforjava.NoiseGen;
import libnoiseforjava.exception.ExceptionInvalidParam;
import libnoiseforjava.module.Perlin;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.data.HexTile;
import org.hexgridapi.core.geometry.builder.coordinate.HexCoordinate;

/**
 *
 * @author roah
 */
public final class ProceduralHexGrid extends Generator {

    /**
     * Module used for the generation.
     */
    private Perlin perlin = new Perlin();
    /**
     * Used to know the minimum/maximum Heigth when generating the map.
     */
    private int heightMin = HexSetting.WATER_LEVEL; // is always count as < 0 (ex: 2 mean -2)
    private int heightMax = 12; // is always count as > 0
    private int textureCount; // todo

    /**
     * @todo textureCount is unused
     * @param seed
     */
    public ProceduralHexGrid(int textureCount) {
        int seed = ProceduralHexGrid.generateSeed();
        if (validateSeed(seed)) {
//            if (heightMin != null) {
//                this.heightMin = (int) (heightMin <= 0 ? FastMath.abs(heightMin) : heightMin);
//            }
//            if (heightMax != null && heightMax > 0) {
//                this.heightMax = heightMax;
//            }
            this.textureCount = textureCount + 1;
//            perlin.setFrequency(2.0);
//            perlin.setPersistence(0.5);
            perlin.setNoiseQuality(NoiseGen.NoiseQuality.QUALITY_FAST);
            perlin.setSeed(seed);
        }
    }

    public void setSeed(int seed) {
        perlin.setSeed(seed);
    }

    /**
     * @return the currently used seed.
     */
    public int getSeed() {
        return perlin.getSeed();
    }

    public HexTile getTileValue(HexCoordinate tilePos) {
        int height = getHeight(tilePos.toOffset().x, tilePos.toOffset().y);
        if (height <= 0) {
            return null;
        }
        return new HexTile(height, 0);
    }

    /**
     *
     * @param param 0 == height <=> 1 == textureKey
     * @return
     */
    private double getTileValue(int posX, int posY, int param) {
        return FastMath.abs((float) perlin.getValue(posX * 0.01, posY * 0.01, param));
    }

    /**
     * @deprecated each project should use his own impl
     */
    private float getCustom(int x, int y, int param) throws IllegalArgumentException {
        if (param == 0 || param == 1) {
            throw new IllegalArgumentException("Value of param : " + param + " is not allowed.");
        }
        return (float) NoiseGen.MakeInt32Range(getTileValue(x, y, param));
    }

    /**
     * @todo wip
     * @param generatedValue
     * @return
     */
    private int getHeight(int x, int y) {
        return (int) NoiseGen.MakeInt32Range((getTileValue(x, y, 0) * (heightMax + heightMin)) - heightMin);
    }

    private int getTexture(int x, int y) {
        int result = (int) NoiseGen.MakeInt32Range((getTileValue(x, y, 1) * (textureCount + 1) - 1));
        if (result == -1) {
            result = -2;
        }
        return result;
    }
}
