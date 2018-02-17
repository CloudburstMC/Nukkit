package cn.nukkit.level.generator.populator;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockGlowstone;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;

public class PopulatorGlowStone extends Populator {

    private ChunkManager level;
    private OreType type = new OreType(new BlockGlowstone(), 1, 20, 128, 10);

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random) {
        this.level = level;
        BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);

        for (int i = 0; i < type.clusterCount; i++) {
            int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
            int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
            int y = this.getHighestWorkableBlock(chunk, x & 0xF, z & 0xF);
            if (y != -1) {
                if (level.getBlockIdAt(x, y, z) != 0) continue;
                type.spawn(level, random, 0, x, y, z);
            }
        }
    }

    private int getHighestWorkableBlock(FullChunk chunk, int x, int z) {
        int y;
        for (y = 127; y >= 0; y--) {
            int b = chunk.getBlockId(x, y, z);
            if (b == Block.AIR) {
                break;
            }
        }
        return y == 0 ? -1 : y;
    }
}
