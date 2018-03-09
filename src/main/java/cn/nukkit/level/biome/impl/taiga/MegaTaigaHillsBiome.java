package cn.nukkit.level.biome.impl.taiga;

import cn.nukkit.level.generator.populator.tree.SpruceBigTreePopulator;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class MegaTaigaHillsBiome extends MegaTaigaBiome {
    public MegaTaigaHillsBiome() {
        super();

        this.setElevation(70, 90);
    }

    @Override
    public String getName() {
        return "Mega Taiga Hills";
    }
}
