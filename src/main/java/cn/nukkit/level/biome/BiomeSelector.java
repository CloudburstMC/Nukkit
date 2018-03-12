package cn.nukkit.level.biome;

import cn.nukkit.level.generator.noise.nukkit.f.SimplexF;
import cn.nukkit.math.NukkitRandom;

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
    private final SimplexF hills;

    public BiomeSelector(NukkitRandom random) {
        this.temperature = new SimplexF(random, 2F, 1F / 8F, 1F / 2048f);
        this.rainfall = new SimplexF(random, 2F, 1F / 8F, 1F / 2048f);
        this.river = new SimplexF(random, 6f, 2 / 4f, 1 / 1024f);
        this.ocean = new SimplexF(random, 6f, 2 / 4f, 1 / 2048f);
        this.hills = new SimplexF(random, 2f, 2 / 4f, 1 / 2048f);
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
        /*double noiseOcean = ocean.noise2D(x, z, true);
        if (noiseOcean < -0.15f) {
            return EnumBiome.OCEAN.biome;
        } else if (noiseOcean < -0.19f) {
            return EnumBiome.STONE_BEACH.biome;
        } else {
            boolean plus = temperature.noise2D(x, z, true) < 0f;
            boolean m = rainfall.noise2D(x, z, true) < 0f;
            if (plus && m) {
                return EnumBiome.EXTREME_HILLS_PLUS_M.biome;
            } else if (m) {
                return EnumBiome.EXTREME_HILLS_M.biome;
            } else if (plus) {
                return EnumBiome.EXTREME_HILLS_PLUS.biome;
            } else {
                return EnumBiome.EXTREME_HILLS.biome;
            }
        }*/

        float noiseOcean = ocean.noise2D(x, z, true);
        float noiseRiver = river.noise2D(x, z, true);
        float temperature = this.temperature.noise2D(x, z, true);
        float rainfall = this.rainfall.noise2D(x, z, true);
        EnumBiome biome;
        if (noiseOcean < -0.15f)    {
            if (noiseOcean < -0.91f)    {
                if (noiseOcean < -0.92f) {
                    biome = EnumBiome.MUSHROOM_ISLAND;
                } else {
                    biome = EnumBiome.MUSHROOM_ISLAND_SHORE;
                }
            } else {
                if (rainfall < 0f)  {
                    biome = EnumBiome.OCEAN;
                } else {
                    biome = EnumBiome.DEEP_OCEAN;
                }
            }
        } else if (Math.abs(noiseRiver) < 0.04f) {
            if (temperature < -0.3f)    {
                biome = EnumBiome.FROZEN_RIVER;
            } else {
                biome = EnumBiome.RIVER;
            }
        } else {
            float hills = this.hills.noise2D(x, z, true);
            if (temperature < -0.5f) {
                //freezing
                if (noiseOcean < -0.12f) {
                    biome = EnumBiome.COLD_BEACH;
                } else if (rainfall < 0f) {
                    if (hills < -0.1f) {
                        biome = EnumBiome.COLD_TAIGA;
                    } else if (hills < 0.5f) {
                        biome = EnumBiome.COLD_TAIGA_HILLS;
                    } else {
                        biome = EnumBiome.COLD_TAIGA_M;
                    }
                } else {
                    if (hills < 0.7f) {
                        biome = EnumBiome.ICE_PLAINS;
                    } else {
                        biome = EnumBiome.ICE_PLAINS_SPIKES;
                    }
                }
            } else if (noiseOcean < -0.12f) {
                biome = EnumBiome.BEACH;
            } else if (temperature < 0f)    {
                //cold
                if (hills < 0.2f)    {
                    if (rainfall < -0.5f)   {
                        biome = EnumBiome.EXTREME_HILLS_M;
                    } else if (rainfall > 0.5f) {
                        biome = EnumBiome.EXTREME_HILLS_PLUS_M;
                    } else if (rainfall < 0f)   {
                        biome = EnumBiome.EXTREME_HILLS;
                    } else {
                        biome = EnumBiome.EXTREME_HILLS_PLUS;
                    }
                } else {
                    if (rainfall < -0.6)    {
                        biome = EnumBiome.MEGA_TAIGA;
                    } else if (rainfall > 0.6)   {
                        biome = EnumBiome.MEGA_SPRUCE_TAIGA;
                    } else if (rainfall < 0.2f)  {
                        biome = EnumBiome.TAIGA;
                    } else {
                        biome = EnumBiome.TAIGA_M;
                    }
                }
            } else if (temperature < 0.5f)  {
                //normal
                if (temperature < 0.25f) {
                    if (rainfall < 0f)  {
                        if (noiseOcean < 0f){
                            biome = EnumBiome.SUNFLOWER_PLAINS;
                        } else {
                            biome = EnumBiome.PLAINS;
                        }
                    } else if (rainfall < 0.25f)    {
                        if (noiseOcean < 0f)    {
                            biome = EnumBiome.FLOWER_FOREST;
                        } else {
                            biome = EnumBiome.FOREST;
                        }
                    } else {
                        if (noiseOcean < 0f)    {
                            biome = EnumBiome.BIRCH_FOREST_M;
                        } else {
                            biome = EnumBiome.BIRCH_FOREST;
                        }
                    }
                } else {
                    if (rainfall < -0.2f)   {
                        if (noiseOcean < 0f)    {
                            biome = EnumBiome.SWAMPLAND_M;
                        } else {
                            biome = EnumBiome.SWAMP;
                        }
                    } else if (rainfall > 0.1f) {
                        if (noiseOcean < 0.155f)  {
                            biome = EnumBiome.JUNGLE_M;
                        } else {
                            biome = EnumBiome.JUNGLE;
                        }
                    } else {
                        if (noiseOcean < 0f)    {
                            biome = EnumBiome.ROOFED_FOREST_M;
                        } else {
                            biome = EnumBiome.ROOFED_FOREST;
                        }
                    }
                }
            } else {
                //hot
                if (rainfall < 0f)  {
                    if (noiseOcean < 0f)    {
                        biome = EnumBiome.DESERT_M;
                    } else if (hills < 0f)    {
                        biome = EnumBiome.DESERT_HILLS;
                    } else {
                        biome = EnumBiome.DESERT;
                    }
                } else if (rainfall > 0.4f)   {
                    if (noiseOcean < 0.155f)    {
                        if (hills < 0f) {
                            biome = EnumBiome.SAVANNA_PLATEAU_M;
                        } else {
                            biome = EnumBiome.SAVANNA_M;
                        }
                    } else {
                        if (hills < 0f) {
                            biome = EnumBiome.SAVANNA_PLATEAU;
                        } else {
                            biome = EnumBiome.SAVANNA;
                        }
                    }
                } else {
                    if (noiseOcean < 0f)    {
                        if (hills < 0f) {
                            biome = EnumBiome.MESA_PLATEAU_F;
                        } else {
                            biome = EnumBiome.MESA_PLATEAU_F_M;
                        }
                    } else if (hills < 0f)  {
                        if (noiseOcean < 0.2f)    {
                            biome = EnumBiome.MESA_PLATEAU_M;
                        } else {
                            biome = EnumBiome.MESA_PLATEAU;
                        }
                    } else {
                        if (noiseOcean < 0.1f)  {
                            biome = EnumBiome.MESA_BRYCE;
                        } else {
                            biome = EnumBiome.MESA;
                        }
                    }
                }
            }
        }

        return biome.biome;
    }
}
