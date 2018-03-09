package cn.nukkit.level.biome.impl.mesa;

import cn.nukkit.block.BlockSand;
import cn.nukkit.level.biome.type.CoveredBiome;
import cn.nukkit.level.generator.noise.Simplex;
import cn.nukkit.level.generator.populator.PopulatorCactus;
import cn.nukkit.level.generator.populator.PopulatorDeadBush;
import cn.nukkit.math.NukkitRandom;

/**
 * @author DaPorkchop_
 */
//porktodo: the tall, flat areas in this biome are tall, thin and nearly vertical
public class MesaBryceBiome extends MesaBiome {
    public MesaBryceBiome() {
        super();
    }

    @Override
    public String getName() {
        return "Mesa (Bryce)";
    }
}
