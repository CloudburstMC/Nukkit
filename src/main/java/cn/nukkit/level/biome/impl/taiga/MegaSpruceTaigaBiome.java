package cn.nukkit.level.biome.impl.taiga;

import cn.nukkit.level.generator.populator.tree.SpruceBigTreePopulator;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class MegaSpruceTaigaBiome extends TaigaBiome {
    public MegaSpruceTaigaBiome() {
        super();

        SpruceBigTreePopulator bigTrees = new SpruceBigTreePopulator();
        bigTrees.setBaseAmount(6);
        this.addPopulator(bigTrees);
    }

    @Override
    public String getName() {
        return "Mega Spruce Taiga";
    }
}
