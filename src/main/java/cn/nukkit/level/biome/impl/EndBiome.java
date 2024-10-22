package cn.nukkit.level.biome.impl;

import cn.nukkit.level.biome.Biome;

public class EndBiome extends Biome {

    public EndBiome() {

    }

    @Override
    public String getName() {
        return "The End";
    }

    @Override
    public boolean canRain() {
        return false;
    }
}
