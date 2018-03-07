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
public abstract class SnowyBiome extends GrassyBiome {
    public SnowyBiome() {
        PopulatorTallGrass tallGrass = new PopulatorTallGrass();
        tallGrass.setBaseAmount(5);
        this.addPopulator(tallGrass);
    }

    @Override
    public int getCoverBlock() {
        return SNOW_LAYER;
    }
}
