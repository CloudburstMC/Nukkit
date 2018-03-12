package cn.nukkit.level.biome;

import cn.nukkit.level.generator.noise.nukkit.f.SimplexF;
import cn.nukkit.math.NukkitRandom;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
//WIP
//do not touch lol
public class BiomeSelector {
    private final SimplexF temperature;
    private final SimplexF rainfall;
    private final SimplexF river;
    private final SimplexF ocean;

    public BiomeSelector(NukkitRandom random) {
        this.temperature = new SimplexF(random, 2F, 1F / 8F, 1F / 512F);
        this.rainfall = new SimplexF(random, 2F, 1F / 8F, 1F / 512F);
        this.river = new SimplexF(random, 6f, 2 / 4f, 1 / 256f);
        this.ocean = new SimplexF(random, 6f, 2 / 4f, 1 / 512f);
        //this.ocean = new Simplex(random, 6d, 2 / 4d, 1 / 64D);
    }

    public Biome pickBiome(int x, int z) {
        /*double noiseOcean = ocean.noise2D(x, z, true);
        double noiseTemp = temperature.noise2D(x, z, true);
        double noiseRain = rainfall.noise2D(x, z, true);
        if (noiseOcean < -0.15) {
            if (noiseOcean < -0.9) {
                return EnumBiome.MUSHROOM_ISLAND.biome;
            } else {
                return EnumBiome.OCEAN.biome;
            }
        }
        double noiseRiver = Math.abs(river.noise2D(x, z, true));
        if (noiseRiver < 0.04) {
            return EnumBiome.RIVER.biome;
        }
        return EnumBiome.OCEAN.biome;*/

        // > using actual biome selectors in 2018
        //x >>= 6;
        //z >>= 6;

        //here's a test for just every biome, for making sure there's no crashes:
        //return Biome.unorderedBiomes.get(Math.abs(((int) x >> 5) ^ 6457109 * ((int) z >> 5) ^ 9800471) % Biome.unorderedBiomes.size());

        //a couple random high primes: 6457109 9800471 7003231

        //here's a test for mesas
        /*boolean doPlateau = ocean.noise2D(x, z, true) < 0f;
        boolean doF = rainfall.noise2D(x, z, true) < -0.5f;
        if (doPlateau)  {
            boolean doM = temperature.noise2D(x, z, true) < 0f;
            if (doM && doF)    {
                return EnumBiome.MESA_PLATEAU_F_M.biome;
            } else if (doM) {
                return EnumBiome.MESA_PLATEAU_M.biome;
            } else if (doF) {
                return EnumBiome.MESA_PLATEAU_F.biome;
            } else {
                return EnumBiome.MESA_PLATEAU.biome;
            }
        } else {
            return doF ? EnumBiome.MESA_BRYCE.biome : EnumBiome.MESA.biome;
        }*/

        //here's a test for extreme hills + oceans
        double noiseOcean = ocean.noise2D(x, z, true);
        if (noiseOcean < -0.15f) {
            return EnumBiome.OCEAN.biome;
        } else if (noiseOcean < -0.19f)  {
            return EnumBiome.STONE_BEACH.biome;
        } else {
            boolean plus = temperature.noise2D(x, z, true) < 0f;
            boolean m = rainfall.noise2D(x, z, true) < 0f;
            if(plus && m)   {
                return EnumBiome.EXTREME_HILLS_PLUS_M.biome;
            } else if (m)   {
                return EnumBiome.EXTREME_HILLS_M.biome;
            } else if (plus)    {
                return EnumBiome.EXTREME_HILLS_PLUS.biome;
            } else {
                return EnumBiome.EXTREME_HILLS.biome;
            }
        }
    }

    public void getBiomes(Biome[] biomes, int x, int z)  {
        for (int xx = 0; xx < 10; xx++)    {
            for (int zz = 0; zz < 10; zz++)    {
                biomes[x + z * 10] = pickBiome(x + xx, z + zz);
            }
        }
    }
}
