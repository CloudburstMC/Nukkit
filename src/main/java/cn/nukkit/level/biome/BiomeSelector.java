package cn.nukkit.level.biome;

import cn.nukkit.level.generator.noise.nukkit.f.SimplexF;
import cn.nukkit.math.NukkitRandom;

/**
 * @author DaPorkchop_
 * Nukkit Project
 */
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
        return this.pickBiome(x, z, 0);
    }

    public Biome pickBiome(int x, int z, int version) {
        float noiseOcean = ocean.noise2D(x, z, true);
        float noiseRiver = river.noise2D(x, z, true);
        float temperature = this.temperature.noise2D(x, z, true);
        float rainfall = this.rainfall.noise2D(x, z, true);

        EnumBiome biome;

        if (version < 2) {
            if (noiseOcean < -0.15f) {
                if (noiseOcean < -0.65f) {
                    biome = EnumBiome.MUSHROOM_ISLAND_SHORE;
                } else {
                    if (rainfall < 0f) {
                        if (temperature < -0.4f) {
                            biome = EnumBiome.FROZEN_OCEAN;
                        } else if (temperature < 0.5f) {
                            biome = EnumBiome.OCEAN;
                        } else {
                            biome = EnumBiome.WARM_OCEAN;
                        }
                    } else {
                        biome = EnumBiome.DEEP_OCEAN;
                    }
                }
            } else if (Math.abs(noiseRiver) < 0.04f) {
                if (temperature < -0.4f) {
                    biome = EnumBiome.FROZEN_RIVER;
                } else {
                    biome = EnumBiome.RIVER;
                }
            } else {
                float hills = this.hills.noise2D(x, z, true);
                if (temperature < -0.379f) {
                    // freezing
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
                } else if (temperature < 0f) {
                    // cold
                    if (hills < 0.2f) {
                        if (rainfall < -0.5f) {
                            biome = EnumBiome.EXTREME_HILLS_M;
                        } else if (rainfall > 0.5f) {
                            biome = EnumBiome.EXTREME_HILLS_PLUS_M;
                        } else if (rainfall < 0f) {
                            biome = EnumBiome.EXTREME_HILLS;
                        } else {
                            biome = EnumBiome.EXTREME_HILLS_PLUS;
                        }
                    } else {
                        if (rainfall < -0.6) {
                            biome = EnumBiome.MEGA_TAIGA;
                        } else if (rainfall > 0.6) {
                            biome = EnumBiome.MEGA_SPRUCE_TAIGA;
                        } else if (rainfall < 0.2f) {
                            biome = EnumBiome.TAIGA;
                        } else {
                            biome = EnumBiome.TAIGA_M;
                        }
                    }
                } else if (temperature < 0.5f) {
                    // normal
                    if (temperature < 0.25f) {
                        if (rainfall < 0f) {
                            if (noiseOcean < 0f) {
                                biome = EnumBiome.SUNFLOWER_PLAINS;
                            } else {
                                biome = EnumBiome.PLAINS;
                            }
                        } else if (rainfall < 0.25f) {
                            if (noiseOcean < 0f) {
                                biome = EnumBiome.FLOWER_FOREST;
                            } else {
                                biome = EnumBiome.FOREST;
                            }
                        } else {
                            if (noiseOcean < 0f) {
                                biome = EnumBiome.BIRCH_FOREST_M;
                            } else {
                                biome = EnumBiome.BIRCH_FOREST;
                            }
                        }
                    } else {
                        if (rainfall < -0.2f) {
                            if (noiseOcean < 0f) {
                                biome = EnumBiome.SWAMPLAND_M;
                            } else {
                                biome = EnumBiome.SWAMP;
                            }
                        } else if (rainfall > 0.1f) {
                            if (noiseOcean < 0.155f) {
                                biome = EnumBiome.JUNGLE_M;
                            } else {
                                biome = EnumBiome.JUNGLE;
                            }
                        } else {
                            if (noiseOcean < 0f) {
                                biome = EnumBiome.ROOFED_FOREST_M;
                            } else {
                                biome = EnumBiome.ROOFED_FOREST;
                            }
                        }
                    }
                } else {
                    // hot
                    if (rainfall < 0f) {
                        if (noiseOcean < 0f) {
                            biome = EnumBiome.DESERT_M;
                        } else if (hills < 0f) {
                            biome = EnumBiome.DESERT_HILLS;
                        } else {
                            biome = EnumBiome.DESERT;
                        }
                    } else if (rainfall > 0.4f) {
                        if (noiseOcean < 0.155f) {
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
                        if (noiseOcean < 0f) {
                            if (hills < 0f) {
                                biome = EnumBiome.MESA_PLATEAU_F;
                            } else {
                                biome = EnumBiome.MESA_PLATEAU_F_M;
                            }
                        } else if (hills < 0f) {
                            if (noiseOcean < 0.2f) {
                                biome = EnumBiome.MESA_PLATEAU_M;
                            } else {
                                biome = EnumBiome.MESA_PLATEAU;
                            }
                        } else {
                            if (noiseOcean < 0.1f) {
                                biome = EnumBiome.MESA_BRYCE;
                            } else {
                                biome = EnumBiome.MESA;
                            }
                        }
                    }
                }
            }
        } else {
            if (noiseOcean < -0.15f) {
                if (noiseOcean < -0.65f) {
                    biome = EnumBiome.MUSHROOM_ISLAND_SHORE;
                } else {
                    if (rainfall < 0f) {
                        if (temperature < -0.45f) {
                            biome = EnumBiome.FROZEN_OCEAN;
                        } else if (temperature < -0.15f) {
                            biome = EnumBiome.COLD_OCEAN;
                        } else if (temperature < 0.2f) {
                            biome = EnumBiome.OCEAN;
                        } else if (temperature < 0.55f) {
                            biome = EnumBiome.LUKEWARM_OCEAN;
                        } else {
                            biome = EnumBiome.WARM_OCEAN;
                        }
                    } else {
                        if (temperature < -0.45f) {
                            biome = EnumBiome.DEEP_FROZEN_OCEAN;
                        } else if (temperature < -0.15f) {
                            biome = EnumBiome.DEEP_COLD_OCEAN;
                        } else if (temperature < 0.2f) {
                            biome = EnumBiome.DEEP_OCEAN;
                        } else if (temperature < 0.55f) {
                            biome = EnumBiome.DEEP_LUKEWARM_OCEAN;
                        } else {
                            biome = EnumBiome.WARM_OCEAN;
                        }
                    }
                }
            } else if (Math.abs(noiseRiver) < 0.04f) {
                if (temperature < -0.379f) {
                    biome = EnumBiome.FROZEN_RIVER;
                } else {
                    biome = EnumBiome.RIVER;
                }
            } else {
                float hills = this.hills.noise2D(x, z, true);
                if (temperature < -0.379f) {
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
                        if (hills < 0) {
                            biome = EnumBiome.ICE_MOUNTAINS;
                        } else if (hills < 0.7f) {
                            biome = EnumBiome.ICE_PLAINS;
                        } else {
                            biome = EnumBiome.ICE_PLAINS_SPIKES;
                        }
                    }
                } else if (noiseOcean < -0.12f) {
                    //if (hills < -0.2225f) {
                    //    biome = EnumBiome.STONE_BEACH;
                    //} else {
                    biome = EnumBiome.BEACH;
                    //}
                } else if (temperature < 0f) {
                    //cold
                    if (hills < 0.2f) {
                        if (rainfall < -0.5f) {
                            biome = EnumBiome.EXTREME_HILLS_M;
                        } else if (rainfall > 0.5f) {
                            biome = EnumBiome.EXTREME_HILLS_PLUS_M;
                        } else if (rainfall < 0f) {
                            biome = EnumBiome.EXTREME_HILLS;
                        } else {
                            biome = EnumBiome.EXTREME_HILLS_PLUS;
                        }
                    } else {
                        if (rainfall < -0.6f) {
                            if (hills < 0.6f) {
                                biome = EnumBiome.MEGA_TAIGA_HILLS;
                            } else {
                                biome = EnumBiome.MEGA_TAIGA;
                            }
                        } else if (rainfall > 0.6f) {
                            if (hills < 0.6f) {
                                biome = EnumBiome.MEGA_SPRUCE_TAIGA_HILLS;
                            } else {
                                biome = EnumBiome.MEGA_SPRUCE_TAIGA;
                            }
                        } else if (rainfall < 0.2f) {
                            if (hills < 0.6f) {
                                biome = EnumBiome.TAIGA_HILLS;
                            } else {
                                biome = EnumBiome.TAIGA;
                            }
                        } else {
                            biome = EnumBiome.TAIGA_M;
                        }
                    }
                } else if (temperature < 0.5f) {
                    //normal
                    if (temperature < 0.25f) {
                        if (rainfall < 0f) {
                            if (noiseOcean < 0f) {
                                biome = EnumBiome.SUNFLOWER_PLAINS;
                            } else {
                                biome = EnumBiome.PLAINS;
                            }
                        } else if (rainfall < 0.25f) {
                            if (noiseOcean < 0f) {
                                biome = EnumBiome.FLOWER_FOREST;
                            } else {
                                if (hills < 0) {
                                    biome = EnumBiome.FOREST_HILLS;
                                } else {
                                    biome = EnumBiome.FOREST;
                                }
                            }
                        } else {
                            if (noiseOcean < 0f) {
                                if (hills < 0) {
                                    biome = EnumBiome.BIRCH_FOREST_HILLS_M;
                                } else {
                                    biome = EnumBiome.BIRCH_FOREST_M;
                                }
                            } else {
                                if (hills < 0) {
                                    biome = EnumBiome.BIRCH_FOREST_HILLS;
                                } else {
                                    biome = EnumBiome.BIRCH_FOREST;
                                }
                            }
                        }
                    } else {
                        if (rainfall < -0.2f) {
                            if (noiseOcean < 0f) {
                                biome = EnumBiome.SWAMPLAND_M;
                            } else {
                                biome = EnumBiome.SWAMP;
                            }
                        } else if (rainfall > 0.1f) {
                            if (noiseOcean < 0.155f) {
                                biome = EnumBiome.JUNGLE_M;
                            } else {
                                if (hills < 0) {
                                    if (rainfall < 0.2f) {
                                        biome = EnumBiome.BAMBOO_JUNGLE_HILLS;
                                    } else {
                                        biome = EnumBiome.JUNGLE_HILLS;
                                    }
                                } else {
                                    if (rainfall < 0.2f) {
                                        biome = EnumBiome.BAMBOO_JUNGLE;
                                    } else {
                                        biome = EnumBiome.JUNGLE;
                                    }
                                }
                            }
                        } else {
                            if (noiseOcean < 0f) {
                                biome = EnumBiome.ROOFED_FOREST_M;
                            } else {
                                biome = EnumBiome.ROOFED_FOREST;
                            }
                        }
                    }
                } else {
                    //hot
                    if (rainfall < 0f) {
                        if (noiseOcean < 0f) {
                            biome = EnumBiome.DESERT_M;
                        } else if (hills < 0f) {
                            biome = EnumBiome.DESERT_HILLS;
                        } else {
                            biome = EnumBiome.DESERT;
                        }
                    } else if (rainfall > 0.4f) {
                        if (noiseOcean < 0.155f) {
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
                        if (noiseOcean < 0f) {
                            if (hills < 0f) {
                                biome = EnumBiome.MESA_PLATEAU_F;
                            } else {
                                biome = EnumBiome.MESA_PLATEAU_F_M;
                            }
                        } else if (hills < 0f) {
                            if (noiseOcean < 0.2f) {
                                biome = EnumBiome.MESA_PLATEAU_M;
                            } else {
                                biome = EnumBiome.MESA_PLATEAU;
                            }
                        } else {
                            if (noiseOcean < 0.1f) {
                                biome = EnumBiome.MESA_BRYCE;
                            } else {
                                biome = EnumBiome.MESA;
                            }
                        }
                    }
                }
            }
        }

        return biome.biome;
    }
}
