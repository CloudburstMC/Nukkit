package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.Wood;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ObjectSpruceTree extends ObjectTree {
    private int treeHeight = 10;

    @Override
    public int getTrunkBlock() {
        return Block.LOG;
    }

    @Override
    public int getLeafBlock() {
        return Block.LEAVES;
    }

    @Override
    public int getType() {
        return Wood.SPRUCE;
    }

    @Override
    public int getTreeHeight() {
        return this.treeHeight;
    }

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        this.treeHeight = random.nextBoundedInt(4) + 6;

        this.placeTrunk(level, x, y, z, random, this.treeHeight - random.nextBoundedInt(2) - 1);

        for (int yy = y - 5 + this.getTreeHeight(); yy <= y + this.getTreeHeight() - 1; ++yy) {
            double yOff = yy - (y + this.getTreeHeight());
            int mid = 1;
            for (int xx = x - mid; xx <= x + mid; ++xx) {
                int xOff = Math.abs(xx - x);
                for (int zz = z - mid; zz <= z + mid; ++zz) {
                    int zOff = Math.abs(zz - z);
                    if (xOff == mid && zOff == mid && (yOff == 0 || random.nextBoundedInt(2) == 0)) {
                        continue;
                    }
                    if (!Block.solid[level.getBlockIdAt(xx, yy, zz)]) {
                        level.setBlockIdAt(xx, yy, zz, this.getLeafBlock());
                        level.setBlockDataAt(xx, yy, zz, this.getType());
                    }
                }
            }
        }

        if (!Block.solid[level.getBlockIdAt(x, this.getTreeHeight(), z)]) {
            level.setBlockIdAt(x, this.getTreeHeight(), z, this.getLeafBlock());
            level.setBlockDataAt(x, this.getTreeHeight(), z, this.getType());
        }
    }
}
