package cn.nukkit.level.generator.populator;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFlower;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;

import java.util.ArrayList;
import java.util.List;

import static cn.nukkit.block.BlockID.DOUBLE_PLANT;

/**
 * author: Angelic47
 * contributer: Niall Lindsay <Niall7459>
 * <p>
 * Nukkit Project
 */

public class PopulatorFlower extends Populator {

    private ChunkManager level;
    private int randomAmount;
    private int baseAmount;

    private final List<int[]> flowerTypes = new ArrayList<>();

    public void setRandomAmount(int randomAmount) {
        this.randomAmount = randomAmount;
    }

    public void setBaseAmount(int baseAmount) {
        this.baseAmount = baseAmount;
    }

    public void addType(int a, int b) {
        int[] c = new int[2];
        c[0] = a;
        c[1] = b;
        this.flowerTypes.add(c);
    }

    public List<int[]> getTypes() {
        return this.flowerTypes;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        this.level = level;
        int amount = random.nextBoundedInt(this.randomAmount + 1) + this.baseAmount;

        if (flowerTypes.size() == 0) {
            this.addType(Block.RED_FLOWER, BlockFlower.TYPE_POPPY);
            this.addType(Block.DANDELION, 0);
        }

        int endNum = this.flowerTypes.size();

        for (int i = 0; i < amount; ++i) {
            int x = NukkitMath.randomRange(random, chunkX * 16, chunkX * 16 + 15);
            int z = NukkitMath.randomRange(random, chunkZ * 16, chunkZ * 16 + 15);
            int y = this.getHighestWorkableBlock(x, z);


            if (y != -1 && this.canFlowerStay(x, y, z)) {
                int[] type = this.flowerTypes.get(random.nextRange(0, endNum - 1));
                this.level.setBlockFullIdAt(x, y, z, (type[0] << 4) | type[1]);
                if (type[0] == DOUBLE_PLANT)    {
                    this.level.setBlockFullIdAt(x, y + 1, z,(type[0] << 4) | (8 | type[1]));
                }
            }
        }
    }

    private boolean canFlowerStay(int x, int y, int z) {
        int b = this.level.getBlockIdAt(x, y, z);
        return (b == Block.AIR || b == Block.SNOW_LAYER) && this.level.getBlockIdAt(x, y - 1, z) == Block.GRASS;
    }

    private int getHighestWorkableBlock(int x, int z) {
        int y;
        for (y = 255; y >= 0; --y) {
            int b = this.level.getBlockIdAt(x, y, z);
            if (b != Block.AIR && b != Block.LEAVES && b != Block.LEAVES2 && b != Block.SNOW_LAYER) {
                break;
            }
        }

        return y == 0 ? -1 : ++y;
    }
}
