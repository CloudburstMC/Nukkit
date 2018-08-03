package cn.nukkit.level.generator.noise.vanilla.d;

import cn.nukkit.math.MathHelper;
import cn.nukkit.math.NukkitRandom;

public class NoiseGeneratorOctavesD {
    /**
     * Collection of noise generation functions.  Output is combined to produce different octaves of noise.
     */
    private final NoiseGeneratorImprovedD[] generatorCollection;
    private final int octaves;

    public NoiseGeneratorOctavesD(NukkitRandom seed, int octavesIn) {
        this.octaves = octavesIn;
        this.generatorCollection = new NoiseGeneratorImprovedD[octavesIn];

        for (int i = 0; i < octavesIn; ++i) {
            this.generatorCollection[i] = new NoiseGeneratorImprovedD(seed);
        }
    }

    /*
     * pars:(par2,3,4=noiseOffset ; so that adjacent noise segments connect) (pars5,6,7=x,y,zArraySize),(pars8,10,12 =
     * x,y,z noiseScale)
     */
    public double[] generateNoiseOctaves(double[] noiseArray, int xOffset, int yOffset, int zOffset, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale) {
        if (noiseArray == null) {
            noiseArray = new double[xSize * ySize * zSize];
        } else {
            for (int i = 0; i < noiseArray.length; ++i) {
                noiseArray[i] = 0.0D;
            }
        }

        double d3 = 1.0D;

        for (int j = 0; j < this.octaves; ++j) {
            double d0 = (double) xOffset * d3 * xScale;
            double d1 = (double) yOffset * d3 * yScale;
            double d2 = (double) zOffset * d3 * zScale;
            long k = MathHelper.floor_double_long(d0);
            long l = MathHelper.floor_double_long(d2);
            d0 = d0 - (double) k;
            d2 = d2 - (double) l;
            k = k % 16777216L;
            l = l % 16777216L;
            d0 = d0 + (double) k;
            d2 = d2 + (double) l;
            this.generatorCollection[j].populateNoiseArray(noiseArray, d0, d1, d2, xSize, ySize, zSize, xScale * d3, yScale * d3, zScale * d3, d3);
            d3 /= 2.0D;
        }

        return noiseArray;
    }

    /*
     * Bouncer function to the main one with some default arguments.
     */
    public double[] generateNoiseOctaves(double[] noiseArray, int xOffset, int zOffset, int xSize, int zSize, double xScale, double zScale, double p_76305_10_) {
        return this.generateNoiseOctaves(noiseArray, xOffset, 10, zOffset, xSize, 1, zSize, xScale, 1.0D, zScale);
    }
}
