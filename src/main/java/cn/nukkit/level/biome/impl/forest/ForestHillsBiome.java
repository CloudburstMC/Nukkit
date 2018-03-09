package cn.nukkit.level.biome.impl.forest;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.PopulatorTree;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class ForestHillsBiome extends ForestBiome {
    public ForestHillsBiome() {
        this(TYPE_NORMAL);
    }

    public ForestHillsBiome(int type) {
        super(type);

        this.setElevation(70, 90);
    }

    @Override
    public String getName() {
        switch (this.type)  {
            case TYPE_BIRCH:
                return "Birch Forest Hills";
            case TYPE_BIRCH_TALL:
                return "Birch Forest Hills M";
            default:
                return "Forest Hills";
        }
    }
}
