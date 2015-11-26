package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.Wood;
import cn.nukkit.level.ChunkManager;

import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class MatchstickSpruceTree extends ObjectTree {
    private int treeHeight = 15;

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
    public void placeObject(ChunkManager level, int x, int y, int z, Random random) {
        this.treeHeight = random.nextInt(10) + 5;

        int topSize = this.getTreeHeight() - (1 + random.nextInt(2));
        int lRadius = 1 + random.nextInt(2);

        this.placeTrunk(level, x, y, z, random, this.getTreeHeight() - 1);

        int radius = random.nextInt(2);
        int maxR = 1;
        int minR = 0;

        for (int yy = 0; yy <= topSize; ++yy) {
            int yyy = y + this.treeHeight - yy;

            for (int xx = x - radius; xx <= x + radius; ++xx) {
                int xOff = Math.abs(xx - x);
                for (int zz = z - radius; zz <= z + radius; ++zz) {
                    int zOff = Math.abs(zz - z);
                    if (xOff == radius && zOff == radius && radius > 0) {
                        continue;
                    }

                    if (!Block.solid[level.getBlockIdAt(xx, yyy, zz)]) {
                        level.setBlockIdAt(xx, yyy, zz, this.getLeafBlock());
                        level.setBlockDataAt(xx, yyy, zz, this.getType());
                    }
                }
            }

            if (radius >= maxR) {
                radius = minR;
                minR = 1;
                if (++maxR > lRadius) {
                    maxR = lRadius;
                }
            } else {
                ++radius;
            }
        }
    }
}
