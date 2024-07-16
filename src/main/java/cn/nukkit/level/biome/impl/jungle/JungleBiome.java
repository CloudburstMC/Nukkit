package cn.nukkit.level.biome.impl.jungle;

import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.impl.PopulatorBamboo;
import cn.nukkit.level.generator.populator.impl.PopulatorMelon;
import cn.nukkit.level.generator.populator.impl.tree.JungleBigTreePopulator;
import cn.nukkit.level.generator.populator.impl.tree.JungleTreePopulator;

/**
 * @author DaPorkchop_
 */
public class JungleBiome extends GrassyBiome {
    public JungleBiome() {
        super();

        JungleTreePopulator trees = new JungleTreePopulator();
        trees.setBaseAmount(10);
        trees.setRandomAmount(10);
        this.addPopulator(trees);

        JungleBigTreePopulator bigTrees = new JungleBigTreePopulator();
        bigTrees.setBaseAmount(8);
        bigTrees.setRandomAmount(8);
        this.addPopulator(bigTrees);

        PopulatorMelon melon = new PopulatorMelon();
        melon.setRandomAmount(2);
        this.addPopulator(melon);

        PopulatorBamboo bamboo = new PopulatorBamboo();
        bamboo.setRandomAmount(2);
        this.addPopulator(bamboo);
    }

    @Override
    public String getName() {
        return "Jungle";
    }
}
