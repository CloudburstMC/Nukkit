package cn.nukkit.level.biome.impl.savanna;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.tree.SavannaTreePopulator;

/**
 * @author DaPorkchop_
 */
public class SavannaPlateauBiome extends SavannaBiome {

    public SavannaPlateauBiome() {
        super();

        this.setElevation(90, 94);
    }

    @Override
    public String getName() {
        return "Savanna Plateau";
    }
}
