package cn.nukkit.level.generator.biome.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.generator.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.MushroomPopulator;

public class MushroomIsland extends GrassyBiome {

    public MushroomIsland() {
        MushroomPopulator mushroomPopulator = new MushroomPopulator();
        mushroomPopulator.setBaseAmount(1);
        addPopulator(mushroomPopulator);

        setElevation(67, 71);

        temperature = 0.9f;
        rainfall = 1.0f;
    }

    @Override
    public String getName() {
        return "Mushroom Island";
    }

    @Override
    public int getSurfaceBlock() {
        return Block.MYCELIUM;
    }
}
