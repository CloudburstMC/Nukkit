package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.object.tree.ObjectTree;
import cn.nukkit.level.generator.populator.type.PopulatorCount;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DaPorkchop_
 * Nukkit Project
 */
public class PopulatorTree extends PopulatorCount {

    private final int type;
    private ChunkManager level;

    public PopulatorTree() {
        this(BlockSapling.OAK);
    }

    public PopulatorTree(int type) {
        this.type = type;
    }

    @Override
    public void populateCount(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        this.level = level;
        int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
        int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
        int y = this.getHighestWorkableBlock(x, z);
        if (y < 3) {
            return;
        }
        ObjectTree.growTree(this.level, x, y, z, random, this.type);

        ThreadLocalRandom r;
        if ((type == BlockSapling.OAK || type == BlockSapling.BIRCH) && (r = ThreadLocalRandom.current()).nextInt(1000) < 2) {
            int bx;
            int bz;
            if (r.nextBoolean()) {
                bx = x;
                bz = z + 1;
            } else if (r.nextBoolean()) {
                bx = x;
                bz = z - 1;
            } else if (r.nextBoolean()) {
                bx = x + 1;
                bz = z;
            } else {
                bx = x - 1;
                bz = z;
            }

            int by = y;
            int maxY = level.getMaxBlockY();
            do {
                if (by >= maxY) {
                    return;
                }

                by++;
            } while (level.getBlockIdAt(bx, by + 1, bz) != Block.LEAVES);

            level.getBlockIdAt(bx, by, bz);
            level.setBlockAt(bx, by, bz, Block.BEE_NEST);

            BlockEntity.createBlockEntity(BlockEntity.BEEHIVE, level.getChunk(chunkX, chunkZ), new CompoundTag()
                    .putString("id", BlockEntity.BEEHIVE)
                    .putInt("x", bx)
                    .putInt("y", by)
                    .putInt("z", bz));
        }
    }

    private int getHighestWorkableBlock(int x, int z) {
        int y;
        for (y = 254; y > 0; --y) {
            int b = this.level.getBlockIdAt(x, y, z);
            if (b == Block.DIRT || b == Block.GRASS || (this.type == BlockSapling.SPRUCE && b == Block.PODZOL)) {
                break;
            } else if (b != Block.AIR && b != Block.SNOW_LAYER) {
                return -1;
            }
        }

        return ++y;
    }
}
