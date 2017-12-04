package cn.nukkit.server.level.generator.biome;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.block.BlockDirt;
import cn.nukkit.server.block.BlockMycelium;
import cn.nukkit.server.level.generator.populator.MushroomPopulator;

public class MushroomIsland extends NormalBiome implements CaveBiome {

    public MushroomIsland() {
        this.setGroundCover(new Block[]{new BlockMycelium(), new BlockDirt(), new BlockDirt(), new BlockDirt(), new BlockDirt()});

        MushroomPopulator mushroomPopulator = new MushroomPopulator();
        mushroomPopulator.setBaseAmount(1);

        addPopulator(mushroomPopulator);
        setElevation(60, 70);
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

    @Override
    public int getGroundBlock() {
        return Block.DIRT;
    }

    @Override
    public int getStoneBlock() {
        return Block.STONE;
    }

}
