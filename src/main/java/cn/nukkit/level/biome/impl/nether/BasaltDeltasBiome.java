package cn.nukkit.level.biome.impl.nether;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.biome.type.CoveredBiome;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.impl.PopulatorBasaltDeltaLava;
import cn.nukkit.level.generator.populator.impl.PopulatorBasaltDeltaMagma;
import cn.nukkit.level.generator.populator.impl.PopulatorBasaltDeltaPillar;
import cn.nukkit.level.generator.populator.impl.PopulatorOre;

public class BasaltDeltasBiome extends CoveredBiome {

    public BasaltDeltasBiome() {
        this.addPopulator(new PopulatorOre(BlockID.BASALT, new OreType[]{
                new OreType(Block.get(BlockID.BLACKSTONE), 4, 128, 0, 128, BASALT)
        }));

        this.addPopulator(new PopulatorBasaltDeltaLava());
        this.addPopulator(new PopulatorBasaltDeltaMagma());
        this.addPopulator(new PopulatorBasaltDeltaPillar());
    }

    @Override
    public String getName() {
        return "Basalt Deltas";
    }

    @Override
    public int getSurfaceId(int x, int y, int z) {
        return Block.BASALT << Block.DATA_BITS;
    }

    @Override
    public int getGroundId(int x, int y, int z) {
        return Block.BASALT << Block.DATA_BITS;
    }

    @Override
    public boolean canRain() {
        return false;
    }
}
