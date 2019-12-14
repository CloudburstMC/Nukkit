package cn.nukkit.level.biome.impl.mushroom;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.impl.MushroomPopulator;

public class MushroomIslandBiome extends GrassyBiome {
    private static final Block MYCELIUM = Block.get(BlockID.MYCELIUM);

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
    public Block getSurface(int x, int y, int z) {
        return MYCELIUM;
    }
}
