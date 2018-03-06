package cn.nukkit.level.generator.biome;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.BlockGrass;
import cn.nukkit.level.generator.populator.PopulatorGrass;
import cn.nukkit.level.generator.populator.PopulatorTallGrass;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class GrassyBiome extends NormalBiome implements CaveBiome {
    public GrassyBiome() {
        PopulatorGrass grass = new PopulatorGrass();
        grass.setBaseAmount(30);
        this.addPopulator(grass);

        PopulatorTallGrass tallGrass = new PopulatorTallGrass();
        tallGrass.setBaseAmount(5);
        this.addPopulator(tallGrass);

        this.setGroundCover(new Block[]{
                new BlockGrass(),
                new BlockDirt(),
                new BlockDirt(),
                new BlockDirt(),
                new BlockDirt()
        });
    }

    @Override
    public int getSurfaceBlock() {
        return Block.GRASS;
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
