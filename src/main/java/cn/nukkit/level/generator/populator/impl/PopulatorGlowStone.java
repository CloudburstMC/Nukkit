package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;

public class PopulatorGlowStone extends Populator {

    @Override
    public void populate(final ChunkManager level, final int chunkX, final int chunkZ, final NukkitRandom random, final FullChunk chunk) {
        final int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
        final int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
        final int y = this.getHighestWorkableBlock(chunk, x & 0xF, z & 0xF);
        if (y != -1 && level.getBlockIdAt(x, y, z) != BlockID.NETHERRACK) {
            final int count = NukkitMath.randomRange(random, 40, 60);
            for (int i = 0; i < count; i++) {
                level.setBlockAt(x + random.nextBoundedInt(7) - 3, y + random.nextBoundedInt(9) - 4, z + random.nextBoundedInt(7) - 3, BlockID.GLOWSTONE);
            }
        }
    }

    private int getHighestWorkableBlock(final FullChunk chunk, final int x, final int z) {
        int y;
        //start scanning a bit lower down to allow space for placing on top
        for (y = 120; y >= 0; y--) {
            final int b = chunk.getBlockId(x, y, z);
            if (b == BlockID.AIR) {
                break;
            }
        }
        return y == 0 ? -1 : y;
    }

}
