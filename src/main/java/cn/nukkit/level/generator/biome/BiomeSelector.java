package cn.nukkit.level.generator.biome;

import cn.nukkit.level.generator.noise.Simplex;

import java.util.Random;
import java.util.TreeMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BiomeSelector {
    private Biome fallback;
    private Simplex temperature;
    private Simplex rainfall;

    private TreeMap<Integer, Biome> biomes = new TreeMap<>();

    private int[] map = new int[64 * 64];

    public BiomeSelector(Random random, Biome fallback) {
        this.fallback = fallback;
        this.temperature = new Simplex(random, 2, 1 / 16, 1 / 512);
        this.rainfall = new Simplex(random, 2, 1 / 16, 1 / 512);
    }

    public int lookup(double... args) {
        return 0;
    }

    public void recalculate() {
        this.map = new int[64 * 64];
        for (int i = 0; i < 64; ++i) {
            for (int j = 0; j < 64; ++j) {
                this.map[i + (j << 6)] = this.lookup(i / 63, j / 63);
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
        int temperature = (int) (this.getTemperature(x, z) * 63);
        int rainfall = (int) (this.getRainfall(x, z) * 63);
        int biomeId = this.map[temperature + (rainfall << 6)];
        return this.biomes.containsKey(biomeId) ? this.biomes.get(biomeId) : this.fallback;
    }

}
