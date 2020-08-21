package cn.nukkit.level.biome.impl.savanna;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
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

        this.setBaseHeight(0.125f);
        this.setHeightVariation(0.05f);
    }

    @Override
    public String getName() {
        return "Savanna";
    }

    @Override
    public boolean canRain() {
        return false;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isDry() {
        return true;
    }
}
