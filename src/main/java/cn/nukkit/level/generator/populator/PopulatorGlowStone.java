package cn.nukkit.level.generator.populator;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;

import static cn.nukkit.block.BlockID.GLOWSTONE;
import static cn.nukkit.block.BlockID.NETHERRACK;

public class PopulatorGlowStone extends Populator {
    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
        int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
        int y = this.getHighestWorkableBlock(chunk, x & 0xF, z & 0xF);
        if (!(y == -1 && level.getBlockIdAt(x, y, z) == NETHERRACK)) {
            int count = NukkitMath.randomRange(random, 20, 40);
            for (int i = 0; i < count; i++) {
                level.setBlockFullIdAt(x + (random.nextBoundedInt(11) - 5), y + (random.nextBoundedInt(9) - 4), z + (random.nextBoundedInt(11) - 5), GLOWSTONE << 4);
            }
        }
    }

    private int getHighestWorkableBlock(FullChunk chunk, int x, int z) {
        int y;
        //start scanning a bit lower down to allow space for placing on top
        for (y = 123; y >= 0; y--) {
            int b = chunk.getBlockId(x, y, z);
            if (b == Block.AIR) {
                break;
            }
        }
        return y == 0 ? -1 : y;
    }
}
