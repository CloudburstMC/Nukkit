package cn.nukkit.level.biome.impl.nether;

import cn.nukkit.block.Block;
import cn.nukkit.level.biome.type.CoveredBiome;
import cn.nukkit.level.generator.populator.impl.PopulatorCrimsonFungus;
import cn.nukkit.level.generator.populator.impl.PopulatorCrimsonForestGround;
import cn.nukkit.level.generator.populator.impl.PopulatorWeepingVines;

public class CrimsonForestBiome extends CoveredBiome {

    public CrimsonForestBiome() {
        this.addPopulator(new PopulatorCrimsonFungus());
        this.addPopulator(new PopulatorCrimsonForestGround());
        this.addPopulator(new PopulatorWeepingVines());
    }

    @Override
    public String getName() {
        return "Crimson Forest";
    }

    @Override
    public int getSurfaceId(int x, int y, int z) {
        return Block.CRIMSON_NYLIUM << Block.DATA_BITS;
    }

    @Override
    public int getGroundId(int x, int y, int z) {
        return Block.NETHERRACK << Block.DATA_BITS;
    }

    @Override
    public boolean canRain() {
        return false;
    }
}
