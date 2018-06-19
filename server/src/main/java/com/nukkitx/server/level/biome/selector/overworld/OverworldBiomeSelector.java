package com.nukkitx.server.level.biome.selector.overworld;

import com.nukkitx.api.level.data.Biome;
import com.nukkitx.server.level.biome.selector.BiomeSelector;
import lombok.NonNull;
import net.daporkchop.lib.noise.Noise;
import net.daporkchop.lib.noise.NoiseEngineType;

import static java.lang.Math.abs;

/**
 * @author DaPorkchop_
 */
public class OverworldBiomeSelector extends BiomeSelector {
    private volatile Noise riverNoise;
    private volatile Noise oceanNoise;

    @Override
    public void init(long seed) {
        this.riverNoise = new Noise(NoiseEngineType.PERLIN, seed, 8, 0.5d, 0.5d);
        this.oceanNoise = new Noise(NoiseEngineType.PERLIN, seed + 1L, 8, 0.5d, 0.5d);
    }

    @Override
    public Biome[] getBiomes(int x, int z, int xWidth, int zWidth, @NonNull Biome[] biomes) {
        if (biomes.length != xWidth * zWidth)   {
            throw new IllegalArgumentException("biome array has invalid length!");
        }

        for (int xx = 0; xx < xWidth; xx++) {
            for (int zz = 0; zz < zWidth; zz++) {
                biomes[xx * zWidth + zz] = this.pick(x + xx, z + zz);
            }
        }
        return biomes;
    }

    private Biome pick(int x, int z) {
        if (oceanNoise.get(x, z) < -0.3d) {
            return Biome.OCEAN;
        } else if (abs(riverNoise.get(x, z)) < 0.04d) {
            return Biome.RIVER;
        }
        return Biome.PLAINS;
    }
}
