package cn.nukkit.level.biome.impl.mesa;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockSand;
import cn.nukkit.level.biome.type.CoveredBiome;
import cn.nukkit.level.generator.noise.nukkit.f.SimplexF;
import cn.nukkit.level.generator.populator.impl.PopulatorCactus;
import cn.nukkit.level.generator.populator.impl.PopulatorDeadBush;
import cn.nukkit.math.BedrockRandom;

import java.util.Arrays;
import java.util.Random;

/**
 * @author DaPorkchop_
 * <p>
 * Handles the placement of stained clay for all mesa variants
 */
public class MesaBiome extends CoveredBiome {
    static final Block[] colorLayer = new Block[64];
    private static final Block RED_SANDSTONE = Block.get(BlockIds.RED_SANDSTONE);
    private static final Block RED_SAND = Block.get(BlockIds.SAND, BlockSand.RED);
    private static final Block[] TERRACOTTA_COLORS = new Block[16];
    static final SimplexF redSandNoise = new SimplexF(new BedrockRandom(937478913), 2f, 1 / 4f, 1 / 4f);
    static final SimplexF colorNoise = new SimplexF(new BedrockRandom(193759875), 2f, 1 / 4f, 1 / 32f);

    static {
        for (int i = 0; i < 16; i++) {
            TERRACOTTA_COLORS[i] = Block.get(BlockIds.STAINED_HARDENED_CLAY, i);
        }
        Random random = new Random(29864);

        Arrays.fill(colorLayer, Block.get(BlockIds.HARDENED_CLAY)); // hard clay, other values are stained clay
        setRandomLayerColor(random, 14, TERRACOTTA_COLORS[1]); // orange
        setRandomLayerColor(random, 8, TERRACOTTA_COLORS[4]); // yellow
        setRandomLayerColor(random, 7, TERRACOTTA_COLORS[12]); // brown
        setRandomLayerColor(random, 10, TERRACOTTA_COLORS[14]); // red
        for (int i = 0, j = 0; i < random.nextInt(3) + 3; i++) {
            j += random.nextInt(6) + 4;
            if (j >= colorLayer.length - 3) {
                break;
            }
            if (random.nextInt(2) == 0 || j < colorLayer.length - 1 && random.nextInt(2) == 0) {
                colorLayer[j - 1] = TERRACOTTA_COLORS[8]; // light gray
            } else {
                colorLayer[j] = TERRACOTTA_COLORS[0]; // white
            }
        }
    }

    private static void setRandomLayerColor(Random random, int sliceCount, Block color) {
        for (int i = 0; i < random.nextInt(4) + sliceCount; i++) {
            int j = random.nextInt(colorLayer.length);
            int k = 0;
            while (k < random.nextInt(2) + 1 && j < colorLayer.length) {
                colorLayer[j++] = color;
                k++;
            }
        }
    }

    private SimplexF moundNoise = new SimplexF(new BedrockRandom(347228794), 2f, 1 / 4f, getMoundFrequency());
    protected int moundHeight;

    public MesaBiome() {
        PopulatorCactus cactus = new PopulatorCactus();
        cactus.setBaseAmount(1);
        cactus.setRandomAmount(1);
        this.addPopulator(cactus);

        PopulatorDeadBush deadBush = new PopulatorDeadBush();
        deadBush.setBaseAmount(3);
        deadBush.setRandomAmount(2);
        this.addPopulator(deadBush);

        this.setMoundHeight(17);
    }

    public void setMoundHeight(int height) {
        this.moundHeight = height;
    }

    @Override
    public int getSurfaceDepth(int x, int y, int z) {
        return y < (71 + Math.round((redSandNoise.noise2D(x, z, true) + 1) * 1.5f)) ? 3 : y - 66;
    }

    @Override
    public Block getSurface(int x, int y, int z) {
        if (y < (71 + Math.round((redSandNoise.noise2D(x, z, true) + 1) * 1.5f))) {
            return RED_SAND;
        } else {
            return colorLayer[(y + Math.round((colorNoise.noise2D(x, z, true) + 1) * 1.5f)) & 0x3F];
        }
    }

    @Override
    public int getGroundDepth(int x, int y, int z) {
        return y < (71 + Math.round((redSandNoise.noise2D(x, z, true) + 1) * 1.5f)) ? 2 : 0;
    }

    @Override
    public Block getGround(int x, int y, int z) {
        return RED_SANDSTONE;
    }

    @Override
    public String getName() {
        return "Mesa";
    }

    protected float getMoundFrequency() {
        return 1 / 128f;
    }

    @Override
    public int getHeightOffset(int x, int z) {
        float n = moundNoise.noise2D(x, z, true);
        float a = minHill();
        return (n > a && n < a + 0.2f) ? (int) ((n - a) * 5f * moundHeight) : n < a + 0.1f ? 0 : moundHeight;
    }

    protected float minHill() {
        return -0.1f;
    }
}
