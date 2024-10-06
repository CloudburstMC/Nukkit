package cn.nukkit.level.biome.impl.jungle;

import cn.nukkit.block.Block;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.noise.nukkit.f.SimplexF;
import cn.nukkit.level.generator.populator.impl.PopulatorBamboo;
import cn.nukkit.level.generator.populator.impl.PopulatorMelon;
import cn.nukkit.level.generator.populator.impl.tree.JungleTreePopulator;
import cn.nukkit.math.NukkitRandom;

public class BambooJungleBiome extends GrassyBiome {

    private static final SimplexF podzolNoise = new SimplexF(new NukkitRandom(), 2f, 1 / 4f, 1 / 32f);

    public BambooJungleBiome() {
        super();

        JungleTreePopulator trees = new JungleTreePopulator();
        trees.setBaseAmount(10);
        this.addPopulator(trees);

        PopulatorMelon melon = new PopulatorMelon();
        melon.setBaseAmount(-65);
        melon.setRandomAmount(70);
        this.addPopulator(melon);

        PopulatorBamboo bamboo = new PopulatorBamboo();
        bamboo.setBaseAmount(64);
        bamboo.setRandomAmount(64);
        this.addPopulator(bamboo);
    }

    @Override
    public String getName() {
        return "Bamboo Jungle";
    }

    @Override
    public int getSurfaceId(int x, int y, int z) {
        return podzolNoise.noise2D(x, z, true) < 0f ? PODZOL << Block.DATA_BITS : Block.GRASS << Block.DATA_BITS;
    }
}
