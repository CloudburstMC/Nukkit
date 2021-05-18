package cn.nukkit.world.biome.impl;

import cn.nukkit.world.biome.Biome;

public class HellBiome extends Biome {
    @Override
    public String getName() {
        return "Hell";
    }

    @Override
    public boolean canRain() {
        return false;
    }
}
