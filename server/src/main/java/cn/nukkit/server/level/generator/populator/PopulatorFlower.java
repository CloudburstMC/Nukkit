package cn.nukkit.server.level.generator.populator;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.block.BlockFlower;
import cn.nukkit.server.level.ChunkManager;
import cn.nukkit.server.math.NukkitMath;
import cn.nukkit.server.math.NukkitRandom;

import java.util.ArrayList;
import java.util.List;

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
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random) {
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
                this.level.setBlockIdAt(x, y, z, type[0]);
                this.level.setBlockDataAt(x, y, z, type[1]);
            }
        }
    }

    private boolean canFlowerStay(int x, int y, int z) {
        int b = this.level.getBlockIdAt(x, y, z);
        return (b == Block.AIR || b == Block.SNOW_LAYER) && this.level.getBlockIdAt(x, y - 1, z) == Block.GRASS;
    }

    private int getHighestWorkableBlock(int x, int z) {
        int y;
        for (y = 127; y >= 0; --y) {
            int b = this.level.getBlockIdAt(x, y, z);
            if (b != Block.AIR && b != Block.LEAVES && b != Block.LEAVES2 && b != Block.SNOW_LAYER) {
                break;
            }
        }

        return y == 0 ? -1 : ++y;
    }
}
