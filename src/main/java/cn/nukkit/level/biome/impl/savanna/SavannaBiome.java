package cn.nukkit.level.biome.impl.savanna;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.impl.tree.SavannaTreePopulator;

/**
 * @author DaPorkchop_
 */
public class SavannaBiome extends GrassyBiome {

    public SavannaBiome() {
        super();

        SavannaTreePopulator tree = new SavannaTreePopulator(BlockSapling.ACACIA);
        tree.setBaseAmount(1);
        this.addPopulator(tree);

        this.setElevation(67, 74);

        this.temperature = 1.2f;
        this.rainfall = 0.0f;
    }

    @Override
    public String getName() {
        return "Savanna";
    }
}
