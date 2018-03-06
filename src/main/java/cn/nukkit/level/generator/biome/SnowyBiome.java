package cn.nukkit.level.generator.biome;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.BlockGrass;
import cn.nukkit.block.BlockSnowLayer;
import cn.nukkit.level.generator.populator.PopulatorTallGrass;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class SnowyBiome extends NormalBiome {
    public SnowyBiome() {
        PopulatorTallGrass tallGrass = new PopulatorTallGrass();
        tallGrass.setBaseAmount(5);
        this.addPopulator(tallGrass);

        this.setGroundCover(new Block[]{
                new BlockSnowLayer(),
                new BlockGrass(),
                new BlockDirt(),
                new BlockDirt(),
                new BlockDirt()
        });
    }
}
