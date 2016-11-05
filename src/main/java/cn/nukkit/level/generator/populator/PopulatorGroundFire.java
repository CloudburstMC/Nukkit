package cn.nukkit.level.generator.populator;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.math.NukkitRandom;

public class PopulatorGroundFire extends Populator {

    private ChunkManager level;
    private int randomAmount;
    private int baseAmount;

    public void setRandomAmount(int amount) {
        this.randomAmount = amount;
    }

    public void setBaseAmount(int amount) {
        this.baseAmount = amount;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random) {
        this.level = level;
        BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);
        int bx = chunkX << 4;
        int bz = chunkZ << 4;
        int tx = bx + 15;
        int tz = bz + 15;
        int amount = random.nextRange(0, this.randomAmount + 1) + this.baseAmount;
        for (int i = 0; i < amount; ++i) {
            int x = random.nextRange(0, 15);
            int z = random.nextRange(0, 15);
            int y = this.getHighestWorkableBlock(chunk, x, z);
            if (y != -1 && this.canGroundFireStay(chunk, x, y, z)) {
                chunk.setBlock(x, y, z, Block.FIRE);
                chunk.setBlockLight(x, y, z, Block.light[Block.FIRE]);
            }
        }
    }

    private boolean canGroundFireStay(FullChunk chunk, int x, int y, int z) {
        int b = chunk.getBlockId(x, y, z);
        return (b == Block.AIR) && chunk.getBlockId(x, y - 1, z) == Block.NETHERRACK;
    }

    private int getHighestWorkableBlock(FullChunk chunk, int x, int z) {
        int y;
        for (y = 0; y <= 127; ++y) {
            int b = chunk.getBlockId(x, y, z);
            if (b == Block.AIR) {
                break;
            }
        }
        return y == 0 ? -1 : y;
    }
}
