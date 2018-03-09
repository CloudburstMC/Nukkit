package cn.nukkit.level.biome.impl.jungle;

import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.tree.JungleBigTreePopulator;
import cn.nukkit.level.generator.populator.tree.JungleTreePopulator;

/**
 * @author DaPorkchop_
 */
//porktodo: melons + pumpkins (GroundBlockPopulator)
public class JungleBiome extends GrassyBiome {
    public JungleBiome() {
        super();

        JungleTreePopulator trees = new JungleTreePopulator();
        trees.setBaseAmount(10);
        this.addPopulator(trees);

        JungleBigTreePopulator bigTrees = new JungleBigTreePopulator();
        bigTrees.setBaseAmount(6);
        this.addPopulator(bigTrees);

        this.setElevation(67, 73);

        this.temperature = 1.2f;
        this.rainfall = 0.9f;
    }

    @Override
    public String getName() {
        return "Jungle";
    }
}
