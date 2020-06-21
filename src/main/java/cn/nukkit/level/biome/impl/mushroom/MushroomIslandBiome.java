package cn.nukkit.level.biome.impl.mushroom;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.impl.MushroomPopulator;

public class MushroomIslandBiome extends GrassyBiome {

    public MushroomIslandBiome() {
        final MushroomPopulator mushroomPopulator = new MushroomPopulator();
        mushroomPopulator.setBaseAmount(1);
        this.addPopulator(mushroomPopulator);

        this.setBaseHeight(0.2f);
        this.setHeightVariation(0.3f);
    }

    @Override
    public String getName() {
        return "Mushroom Island";
    }

    @Override
    public int getSurfaceBlock(final int y) {
        return BlockID.MYCELIUM;
    }

}
