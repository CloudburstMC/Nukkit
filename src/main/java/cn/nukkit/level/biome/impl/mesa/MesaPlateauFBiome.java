package cn.nukkit.level.biome.impl.mesa;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.generator.populator.impl.PopulatorTree;

/**
 * @author DaPorkchop_
 */
public class MesaPlateauFBiome extends MesaPlateauBiome {
    private static final Block GRASS = Block.get(BlockID.GRASS);

    public MesaPlateauFBiome() {
        super();

        PopulatorTree tree = new PopulatorTree(BlockSapling.OAK);
        tree.setBaseAmount(2);
        tree.setRandomAmount(1);
        this.addPopulator(tree);
    }

    @Override
    public Block getCover(int x, int z) {
        return GRASS;
    }

    @Override
    public String getName() {
        return "Mesa Plateau F";
    }
}
