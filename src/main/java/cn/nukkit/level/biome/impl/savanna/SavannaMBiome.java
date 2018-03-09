package cn.nukkit.level.biome.impl.savanna;

import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.noise.Simplex;
import cn.nukkit.level.generator.populator.tree.SavannaTreePopulator;
import cn.nukkit.math.NukkitRandom;

/**
 * @author DaPorkchop_
 */
public class SavannaMBiome extends SavannaBiome {
    //TODO: coarse dirt?
    //private static final Simplex coarseDirtNoise = new Simplex(new NukkitRandom(0), 1d, 1 / 4f, 1 / 16f);
    //boolean coarseDirt = false;

    public SavannaMBiome() {
        super();

        //this is set to be above the build limit so that it'll actually hit it sometimes
        this.setElevation(67, 260);
    }

    @Override
    public String getName() {
        return "Savanna M";
    }

    //@Override
    //public int getSurfaceBlock(int y) {
    //    return coarseDirt ? COARSE_DIRT : DIRT;
    //}

    //@Override
    //public void preCover(int x, int z) {
    //    coarseDirt = coarseDirtNoise.noise2D(x, z, true) < 0;
    //}
}
