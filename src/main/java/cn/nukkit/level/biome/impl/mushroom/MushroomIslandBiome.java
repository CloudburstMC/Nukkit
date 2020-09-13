package cn.nukkit.level.biome.impl.mushroom;

import cn.nukkit.api.RemovedFromNewRakNet;
import cn.nukkit.api.Since;
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

    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    @Override
    public int getSurfaceBlock(int y) {
        if (useNewRakNetSurface()) {
            return getSurfaceId(0,y,0) >> 4;
        }
        return Block.MYCELIUM;
    }
}
