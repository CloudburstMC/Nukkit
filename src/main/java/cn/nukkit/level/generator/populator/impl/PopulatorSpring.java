package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;

public class PopulatorSpring extends Populator {

    private final int block;
    private final int replaceId;
    private final int attempts;
    private final int minHeight;
    private final int maxHeight;

    public PopulatorSpring(int block, int replaceId, int attempts, int minHeight, int maxHeight) {
        this.block = block;
        this.replaceId = replaceId;
        this.attempts = attempts;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int sourceX = chunkX << 4;
        int sourceZ = chunkZ << 4;

        for (int i = 0; i < attempts; i++) {
            int x = sourceX + random.nextBoundedInt(16);
            int z = sourceZ + random.nextBoundedInt(16);
            int y = random.nextRange(minHeight, maxHeight);

            int blockXYZ = level.getBlockIdAt(x, y, z);
            if (!(blockXYZ == BlockID.AIR || blockXYZ == this.replaceId)) {
                continue;
            }

            // We don't want to trigger block update next to a fallable block

            int blockX_1YZ = level.getBlockIdAt(x, y - 1, z);
            if (blockX_1YZ == BlockID.SAND || blockX_1YZ == BlockID.GRAVEL) {
                continue;
            }

            int blockX1YZ = level.getBlockIdAt(x, y + 1, z);
            if (blockX1YZ == BlockID.SAND || blockX1YZ == BlockID.GRAVEL) {
                continue;
            }

            if (!(blockX_1YZ == this.replaceId || blockX1YZ == this.replaceId)) {
                continue;
            }

            int block1XYZ = level.getBlockIdAt(x + 1, y, z);
            if (block1XYZ == BlockID.SAND || block1XYZ == BlockID.GRAVEL) {
                continue;
            }
            int block_1XYZ = level.getBlockIdAt(x - 1, y, z);
            if (block_1XYZ == BlockID.SAND || block_1XYZ == BlockID.GRAVEL) {
                continue;
            }
            int blockXY1Z = level.getBlockIdAt(x, y, z + 1);
            if (blockXY1Z == BlockID.SAND || blockXY1Z == BlockID.GRAVEL) {
                continue;
            }
            int blockXY_1Z = level.getBlockIdAt(x, y, z - 1);
            if (blockXY_1Z == BlockID.SAND || blockXY_1Z == BlockID.GRAVEL) {
                continue;
            }

            int surroundCount = 0;
            if (block1XYZ == this.replaceId) surroundCount++;
            if (block_1XYZ == this.replaceId) surroundCount++;
            if (blockXY1Z == this.replaceId) surroundCount++;
            if (blockXY_1Z == this.replaceId) surroundCount++;

            if (surroundCount != 3) {
                continue;
            }

            int airCount = 0;
            if (block1XYZ == AIR) airCount++;
            if (block_1XYZ == AIR) airCount++;
            if (blockXY1Z == AIR) airCount++;
            if (blockXY_1Z == AIR) airCount++;

            if (airCount != 1) {
                continue;
            }

            level.setBlockAt(x, y, z, this.block);

            LevelProvider provider = chunk.getProvider();
            if (provider != null) {
                Block state = Block.fullList[this.block << Block.DATA_BITS].clone();
                state.x = x;
                state.y = y;
                state.z = z;
                state.level = provider.getLevel();
                if (state.level != null) {
                    state.level.scheduleUpdate(state, state, 1, 0, false);
                }
            }
        }
    }
}
