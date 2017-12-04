package cn.nukkit.server.level.generator.populator;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.block.BlockGlowstone;
import cn.nukkit.server.level.ChunkManager;
import cn.nukkit.server.level.format.FullChunk;
import cn.nukkit.server.level.format.generic.BaseFullChunk;
import cn.nukkit.server.level.generator.object.ore.ObjectOre;
import cn.nukkit.server.level.generator.object.ore.OreType;
import cn.nukkit.server.math.NukkitRandom;

public class PopulatorGlowStone extends Populator {

    private ChunkManager level;
    private OreType type = new OreType(new BlockGlowstone(), 1, 20, 128, 10);

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random) {
        this.level = level;
        BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);
        int bx = chunkX << 4;
        int bz = chunkZ << 4;
        int tx = bx + 15;
        int tz = bz + 15;
        ObjectOre ore = new ObjectOre(random, type, Block.AIR);
        for (int i = 0; i < ore.type.clusterCount; ++i) {
            int x = random.nextRange(0, 15);
            int z = random.nextRange(0, 15);
            int y = this.getHighestWorkableBlock(chunk, x, z);
            if (y != -1) {
                ore.placeObject(level, bx + x, y, bz + z);
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
