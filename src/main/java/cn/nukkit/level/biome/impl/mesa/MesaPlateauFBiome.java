package cn.nukkit.level.biome.impl.mesa;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.generator.populator.PopulatorTree;

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

    @Override
    public int getCoverBlock() {
        return GRASS;
    }

    @Override
    public String getName() {
        return "Mesa Plateau F";
    }
}
