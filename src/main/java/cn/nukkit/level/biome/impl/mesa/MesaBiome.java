package cn.nukkit.level.biome.impl.mesa;

import cn.nukkit.block.BlockSand;
import cn.nukkit.level.biome.type.CoveredBiome;
import cn.nukkit.level.generator.noise.Simplex;
import cn.nukkit.level.generator.populator.PopulatorCactus;
import cn.nukkit.level.generator.populator.PopulatorDeadBush;
import cn.nukkit.math.NukkitRandom;

/**
 * @author DaPorkchop_
 * <p>
 * Handles the placement of stained clay for all mesa variants
 */
//porktodo: have biomes be able to offset their own height
public class MesaBiome extends CoveredBiome {
    static final int[] colors;
    static final Simplex quickNoise = new Simplex(new NukkitRandom(0), 1d, 1 / 4F, 1 / 3.5F);

    static {
        colors = new int[7];
        //0: white
        //1: orange
        //4: yellow
        //8: light gray
        //12: brown
        //14: red
        //-1: use plain terracotta instead of stained terracotta
        colors[0] = 0;
        colors[1] = 1;
        colors[2] = 4;
        colors[3] = 8;
        colors[4] = 12;
        colors[5] = 14;
        colors[6] = -1;
    }

    int randY = 0;
    int redSandThreshold = 0;
    boolean isRedSand = false;
    //cache this too so we can access it in getSurfaceBlock and getSurfaceMeta without needing to calculate it twice
    int currMeta = 0;
    int startY = 0;

    public MesaBiome() {
        PopulatorCactus cactus = new PopulatorCactus();
        cactus.setBaseAmount(1);
        cactus.setRandomAmount(1);
        this.addPopulator(cactus);

        PopulatorDeadBush deadBush = new PopulatorDeadBush();
        deadBush.setBaseAmount(3);
        deadBush.setRandomAmount(2);
        this.addPopulator(deadBush);

        this.setElevation(67, 71);
    }

    @Override
    public int getSurfaceDepth(int y) {
        isRedSand = y < redSandThreshold;
        startY = y;
        //if true, we'll be generating red sand
        return isRedSand ? 3 : 14;
    }

    @Override
    public int getSurfaceBlock(int y) {
        if (isRedSand) {
            return SAND;
        } else {
            currMeta = colors[(startY - (y + randY)) % colors.length];
            return currMeta == -1 ? TERRACOTTA : STAINED_TERRACOTTA;
        }
    }

    @Override
    public int getSurfaceMeta(int y) {
        if (isRedSand) {
            return BlockSand.RED;
        } else {
            return Math.max(0, currMeta);
        }
    }

    @Override
    public int getGroundDepth(int y) {
        return isRedSand ? 2 : 0;
    }

    @Override
    public int getGroundBlock(int y) {
        return RED_SANDSTONE;
    }

    @Override
    public String getName() {
        return "Mesa";
    }

    @Override
    public void preCover(int x, int z) {
        //random noise from 0-3
        randY = (int) ((quickNoise.noise2D(x, z, true) + 1) * 1.5D);
        redSandThreshold = 71 + randY;
    }
}
