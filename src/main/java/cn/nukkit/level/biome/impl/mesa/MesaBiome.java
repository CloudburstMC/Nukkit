package cn.nukkit.level.biome.impl.mesa;

import cn.nukkit.block.BlockSand;
import cn.nukkit.level.biome.type.CoveredBiome;
import cn.nukkit.level.generator.noise.SimplexF;
import cn.nukkit.level.generator.populator.impl.PopulatorCactus;
import cn.nukkit.level.generator.populator.impl.PopulatorDeadBush;
import cn.nukkit.math.NukkitRandom;

import java.util.Arrays;
import java.util.Random;

/**
 * @author DaPorkchop_
 * <p>
 * Handles the placement of stained clay for all mesa variants
 */
public class MesaBiome extends CoveredBiome {
    static final int[] colorLayer = new int[64];
    static final SimplexF redSandNoise = new SimplexF(new NukkitRandom(937478913), 2f, 1 / 4f, 1 / 4f);
    static final SimplexF colorNoise = new SimplexF(new NukkitRandom(193759875), 2f, 1 / 4f, 1 / 32f);
    private SimplexF moundNoise = new SimplexF(new NukkitRandom(347228794), 2f, 1 / 4f, getMoundFrequency());
    protected int moundHeight;

    static {
        Random random = new Random(29864);

        Arrays.fill(colorLayer, -1); // hard clay, other values are stained clay
        setRandomLayerColor(random, 14, 1); // orange
        setRandomLayerColor(random, 8, 4); // yellow
        setRandomLayerColor(random, 7, 12); // brown
        setRandomLayerColor(random, 10, 14); // red
        for (int i = 0, j = 0; i < random.nextInt(3) + 3; i++) {
            j += random.nextInt(6) + 4;
            if (j >= colorLayer.length -3) {
                break;
            }
            if (random.nextInt(2) == 0 || j < colorLayer.length - 1 && random.nextInt(2) == 0) {
                colorLayer[j - 1] = 8; // light gray
            } else {
                colorLayer[j] = 0; // white
            }
        }
    }

    private static void setRandomLayerColor(Random random, int sliceCount, int color) {
        for (int i = 0; i < random.nextInt(4) + sliceCount; i++) {
            int j = random.nextInt(colorLayer.length);
            int k = 0;
            while (k < random.nextInt(2) + 1 && j < colorLayer.length) {
                colorLayer[j++] = color;
                k++;
            }
        }
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

        this.setMoundHeight(17);
    }

    public void setMoundHeight(int height)  {
        this.moundHeight = height;
    }

    @Override
    public int getSurfaceDepth(int y) {
        isRedSand = y < redSandThreshold;
        startY = y;
        //if true, we'll be generating red sand
        return isRedSand ? 3 : y - 66;
    }

    @Override
    public int getSurfaceBlock(int y) {
        if (isRedSand) {
            return SAND;
        } else {
            currMeta = colorLayer[(y + randY) & 0x3F];
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
        randY = Math.round((colorNoise.noise2D(x, z, true) + 1) * 1.5f);
        redSandThreshold = 71 + Math.round((redSandNoise.noise2D(x, z, true) + 1) * 1.5f);
    }

    protected float getMoundFrequency()    {
        return 1 / 128f;
    }

    @Override
    public int getHeightOffset(int x, int z) {
        float n = moundNoise.noise2D(x, z, true);
        float a = minHill();
        return (n > a && n < a + 0.2f) ? (int) ((n - a) * 5f * moundHeight) : n < a + 0.1f ? 0 : moundHeight;
    }

    protected float minHill()   {
        return -0.1f;
    }
}
