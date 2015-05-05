package org.hexgridapi.core.mapgenerator;

import java.util.Random;

/**
 *
 * @author roah
 */
public class Generator {
    private static Random GENERATOR = new Random(System.currentTimeMillis());

    public static int generateSeed() {
        return (int) (100000000 + GENERATOR.nextDouble() * 900000000);
    }

    protected boolean validateSeed(int seed) {
        String s = String.valueOf(seed);
        if (s.toCharArray().length == 9 && s.matches("\\d{9}")) {
            return true;
        }
        return false;
    }
}
