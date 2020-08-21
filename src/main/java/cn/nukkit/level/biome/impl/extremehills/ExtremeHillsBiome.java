package cn.nukkit.level.biome.impl.extremehills;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.impl.PopulatorTree;

/**
 * @author DaPorkchop_ (Nukkit Project)
 * <p>
 * make sure this is touching another extreme hills type or it'll look dumb
 *
 * steep mountains with flat areas between
 */
public class ExtremeHillsBiome extends GrassyBiome {
    public ExtremeHillsBiome() {
        this(true);
    }

    public ExtremeHillsBiome(boolean tree) {
        super();

        if (tree) {
            PopulatorTree trees = new PopulatorTree(BlockSapling.SPRUCE);
            trees.setBaseAmount(2);
            trees.setRandomAmount(2);
            this.addPopulator(trees);
        }

        this.setBaseHeight(1f);
        this.setHeightVariation(0.5f);
    }

    @Override
    public String getName() {
        return "Extreme Hills";
    }

    @Override
    public boolean doesOverhang() {
        return true;
    }
}
