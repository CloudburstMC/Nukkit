package cn.nukkit.level.biome.impl.mushroom;

import cn.nukkit.block.Block;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.impl.MushroomPopulator;

public class MushroomIslandBiome extends GrassyBiome {
    public MushroomIslandBiome() {
        MushroomPopulator mushroomPopulator = new MushroomPopulator();
        mushroomPopulator.setBaseAmount(1);
        addPopulator(mushroomPopulator);

        this.setBaseHeight(0.2f);
        this.setHeightVariation(0.3f);
    }

    @Override
    public String getName() {
        return "Mushroom Island";
    }

    @Override
    public int getSurfaceBlock(int y) {
        return Block.MYCELIUM;
    }
}
