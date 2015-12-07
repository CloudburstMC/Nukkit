package cn.nukkit.level.generator.biome;

import cn.nukkit.level.generator.noise.Simplex;

import java.util.HashMap;
import java.util.Map;
import cn.nukkit.utils.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BiomeSelector {
    private Biome fallback;
    private Simplex temperature;
    private Simplex rainfall;

    private Map<Integer, Biome> biomes = new HashMap<>();

    private int[] map = new int[64 * 64];

    public BiomeSelector(Random random, Biome fallback) {
        this.fallback = fallback;
        this.temperature = new Simplex(random, 2d, 1 / 16d, 1 / 512d);
        this.rainfall = new Simplex(random, 2d, 1 / 16d, 1 / 512d);
    }

    public int lookup(double temperature, double rainfall) {
        if (rainfall < 0.25) {
            if (temperature < 0.7) {
                return Biome.OCEAN;
            } else if (temperature < 0.85) {
                return Biome.RIVER;
            } else {
                return Biome.SWAMP;
            }
        } else if (rainfall < 0.60) {
            if (temperature < 0.25) {
                return Biome.ICE_PLAINS;
            } else if (temperature < 0.75) {
                return Biome.PLAINS;
            } else {
                return Biome.DESERT;
            }
        } else if (rainfall < 0.80) {
            if (temperature < 0.25) {
                return Biome.TAIGA;
            } else if (temperature < 0.75) {
                return Biome.FOREST;
            } else {
                return Biome.BIRCH_FOREST;
            }
        } else {
            if (temperature < 0.25) {
                return Biome.MOUNTAINS;
            } else if (temperature < 0.70) {
                return Biome.SMALL_MOUNTAINS;
            } else {
                return Biome.RIVER;
            }
        }
    }

    public void recalculate() {
        this.map = new int[64 * 64];
        for (int i = 0; i < 64; ++i) {
            for (int j = 0; j < 64; ++j) {
                this.map[i + (j << 6)] = this.lookup(i / 63d, j / 63d);
            }
        }
    }

    public void addBiome(Biome biome) {
        this.biomes.put(biome.getId(), biome);
    }

    public double getTemperature(double x, double z) {
        return (this.temperature.noise2D(x, z, true) + 1) / 2;
    }

    public double getRainfall(double x, double z) {
        return (this.rainfall.noise2D(x, z, true) + 1) / 2;
    }

    public Biome pickBiome(double x, double z) {
        int temperature = (int) (this.getTemperature(x, z) * 63d);
        int rainfall = (int) (this.getRainfall(x, z) * 63d);

        int biomeId = this.map[temperature + (rainfall << 6)];
        return this.biomes.containsKey(biomeId) ? this.biomes.get(biomeId) : this.fallback;
    }

}
