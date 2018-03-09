package cn.nukkit.level.biome.impl.plains;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDoublePlant;
import cn.nukkit.block.BlockFlower;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.PopulatorDoublePlant;
import cn.nukkit.level.generator.populator.PopulatorFlower;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class SunflowerPlainsBiome extends PlainsBiome {

    public SunflowerPlainsBiome() {
        super();

        PopulatorDoublePlant sunflower = new PopulatorDoublePlant(BlockDoublePlant.SUNFLOWER);
        sunflower.setBaseAmount(8);
        sunflower.setRandomAmount(5);
        this.addPopulator(sunflower);
    }

    @Override
    public String getName() {
        return "Sunflower Plains";
    }
}
