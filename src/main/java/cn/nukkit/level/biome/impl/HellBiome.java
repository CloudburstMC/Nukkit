package cn.nukkit.level.biome.impl;

import cn.nukkit.level.biome.Biome;

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
