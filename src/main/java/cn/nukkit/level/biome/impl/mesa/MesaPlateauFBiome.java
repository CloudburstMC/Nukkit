package cn.nukkit.level.biome.impl.mesa;

import cn.nukkit.api.RemovedFromNewRakNet;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.generator.populator.impl.PopulatorTree;

/**
 * @author DaPorkchop_
 */
public class MesaPlateauFBiome extends MesaPlateauBiome {
    public MesaPlateauFBiome() {
        super();

        PopulatorTree tree = new PopulatorTree(BlockSapling.OAK);
        tree.setBaseAmount(2);
        tree.setRandomAmount(1);
        this.addPopulator(tree);
    }

    @Since("1.4.0.0-PN")
    @Override
    @RemovedFromNewRakNet
    public int getCoverBlock() {
        if (useNewRakNetCover()) {
            return getCoverId(0,0) >> 4;
        }
        return GRASS;
    }

    @Override
    public String getName() {
        return "Mesa Plateau F";
    }
}
