package cn.nukkit.level.biome.impl.mesa;

import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockSand;
import cn.nukkit.level.biome.type.CoveredBiome;
import cn.nukkit.level.generator.noise.nukkit.f.SimplexF;
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

    static {
        final Random random = new Random(29864);

        Arrays.fill(MesaBiome.colorLayer, -1); // hard clay, other values are stained clay
        MesaBiome.setRandomLayerColor(random, 14, 1); // orange
        MesaBiome.setRandomLayerColor(random, 8, 4); // yellow
        MesaBiome.setRandomLayerColor(random, 7, 12); // brown
        MesaBiome.setRandomLayerColor(random, 10, 14); // red
        for (int i = 0, j = 0; i < random.nextInt(3) + 3; i++) {
            j += random.nextInt(6) + 4;
            if (j >= MesaBiome.colorLayer.length - 3) {
                break;
            }
            if (random.nextInt(2) == 0 || j < MesaBiome.colorLayer.length - 1 && random.nextInt(2) == 0) {
                MesaBiome.colorLayer[j - 1] = 8; // light gray
            } else {
                MesaBiome.colorLayer[j] = 0; // white
            }
        }
    }

    private final SimplexF moundNoise = new SimplexF(new NukkitRandom(347228794), 2f, 1 / 4f, this.getMoundFrequency());

    int randY = 0;

    int redSandThreshold = 0;

    boolean isRedSand = false;

    //cache this too so we can access it in getSurfaceBlock and getSurfaceMeta without needing to calculate it twice
    int currMeta = 0;

    int startY = 0;

    protected int moundHeight;

    public MesaBiome() {
        final PopulatorCactus cactus = new PopulatorCactus();
        cactus.setBaseAmount(1);
        cactus.setRandomAmount(1);
        this.addPopulator(cactus);

        final PopulatorDeadBush deadBush = new PopulatorDeadBush();
        deadBush.setBaseAmount(3);
        deadBush.setRandomAmount(2);
        this.addPopulator(deadBush);

        this.setMoundHeight(17);
    }

    private static void setRandomLayerColor(final Random random, final int sliceCount, final int color) {
        for (int i = 0; i < random.nextInt(4) + sliceCount; i++) {
            int j = random.nextInt(MesaBiome.colorLayer.length);
            int k = 0;
            while (k < random.nextInt(2) + 1 && j < MesaBiome.colorLayer.length) {
                MesaBiome.colorLayer[j++] = color;
                k++;
            }
        }
    }

    public void setMoundHeight(final int height) {
        this.moundHeight = height;
    }

    @Override
    public int getSurfaceDepth(final int y) {
        this.isRedSand = y < this.redSandThreshold;
        this.startY = y;
        //if true, we'll be generating red sand
        return this.isRedSand ? 3 : y - 66;
    }

    @Override
    public int getSurfaceBlock(final int y) {
        if (this.isRedSand) {
            return BlockID.SAND;
        } else {
            this.currMeta = MesaBiome.colorLayer[y + this.randY & 0x3F];
            return this.currMeta == -1 ? BlockID.TERRACOTTA : BlockID.STAINED_TERRACOTTA;
        }
    }

    @Override
    public int getSurfaceMeta(final int y) {
        if (this.isRedSand) {
            return BlockSand.RED;
        } else {
            return Math.max(0, this.currMeta);
        }
    }

    @Override
    public int getGroundDepth(final int y) {
        return this.isRedSand ? 2 : 0;
    }

    @Override
    public int getGroundBlock(final int y) {
        return BlockID.RED_SANDSTONE;
    }

    @Override
    public void preCover(final int x, final int z) {
        //random noise from 0-3
        this.randY = Math.round((MesaBiome.colorNoise.noise2D(x, z, true) + 1) * 1.5f);
        this.redSandThreshold = 71 + Math.round((MesaBiome.redSandNoise.noise2D(x, z, true) + 1) * 1.5f);
    }

    @Override
    public String getName() {
        return "Mesa";
    }

    @Override
    public int getHeightOffset(final int x, final int z) {
        final float n = this.moundNoise.noise2D(x, z, true);
        final float a = this.minHill();
        return n > a && n < a + 0.2f ? (int) ((n - a) * 5f * this.moundHeight) : n < a + 0.1f ? 0 : this.moundHeight;
    }

    @Override
    public boolean canRain() {
        return false;
    }

    protected float getMoundFrequency() {
        return 1 / 128f;
    }

    protected float minHill() {
        return -0.1f;
    }

}
