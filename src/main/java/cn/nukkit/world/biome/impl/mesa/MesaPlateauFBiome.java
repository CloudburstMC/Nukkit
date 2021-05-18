package cn.nukkit.world.biome.impl.mesa;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.world.generator.populator.impl.PopulatorTree;

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
    public int getCoverId(int x, int z) {
        return GRASS << 4;
    }

    @Override
    public String getName() {
        return "Mesa Plateau F";
    }
}
