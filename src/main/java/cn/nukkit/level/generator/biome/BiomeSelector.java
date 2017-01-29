package cn.nukkit.level.generator.biome;

import cn.nukkit.level.generator.Normal;
import cn.nukkit.level.generator.biome.Biome;
import cn.nukkit.level.generator.noise.Simplex;
import cn.nukkit.math.NukkitRandom;

import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BiomeSelector {
    private final Biome fallback;
    private final Simplex temperature;
    private final Simplex rainfall;

    private final Map<Integer, Biome> biomes = new HashMap<>();

    private int[] map = new int[64 * 64];

    public BiomeSelector(NukkitRandom random, Biome fallback) {
        this.fallback = fallback;
        this.temperature = new Simplex(random, 2F, 1F / 8F, 1F / 1024F);
        this.rainfall = new Simplex(random, 2F, 1F / 8F, 1F / 1024F);
    }

    public int lookup(double temperature, double rainfall) {
       if (temperature < 0.8f) {
            if (rainfall < 0.4f) {
                return Biome.PLAINS;
            } else if (rainfall < 0.9f) {
                return Biome.SWAMP;
            } else {
                if (temperature < 2.0f) {
                    if (rainfall == 0.0f) {
                        return Biome.DESERT;
                    }
                }
            }
        } else if (temperature < 1.2f) {
            if (rainfall < 0.9f) {
                return Biome.JUNGLE;
            } else {
                if (rainfall == 0.0f) {
                    return Biome.SAVANNA;
                }
            }
        } else if (temperature < 0.05f) {
            if (rainfall < 0.8f) {
                return Biome.TAIGA;
            }
        } else if (temperature < 0.6f) {
            if (rainfall < 0.6f) {
                return Biome.BIRCH_FOREST;
            }
        } else if (temperature < 0.9f) {
            if (rainfall < 1.0f) {
                return Biome.MUSHROOM_ISLAND;
            }
        } else if (temperature == 0.0f) {
            if (rainfall < 0.5f) {
                return Biome.ICE_PLAINS;
            }
        } else if (temperature < 0.7f) {
            if (rainfall < 0.8f) {
                return Biome.FOREST;
            } else {
                return Biome.ROOFED_FOREST;
            }
        } else {
            return Biome.ROOFED_FOREST_M;
        }
    return Biome.OCEAN;
    }
    
    public void recalculate() {
        this.map = new int[64 * 64];
        for(int i = 0; i < 64; ++i) {
            for(int j = 0; j < 64; ++j) {
                this.map[i + (j << 6)] = this.lookup(i / 63d, j / 63d);
            }
        }
    }

    public void addBiome(Biome biome) {
        this.biomes.put(Integer.valueOf(biome.getId()), biome);
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
