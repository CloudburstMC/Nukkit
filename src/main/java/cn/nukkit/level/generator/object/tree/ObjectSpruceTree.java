package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockWood;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ObjectSpruceTree extends ObjectTree {
    protected int treeHeight;

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
        return BlockWood.SPRUCE;
    }

    @Override
    public int getTreeHeight() {
        return this.treeHeight;
    }

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        this.treeHeight = random.nextBoundedInt(4) + 6;

        int topSize = this.getTreeHeight() - (1 + random.nextBoundedInt(2));
        int lRadius = 2 + random.nextBoundedInt(2);

        this.placeTrunk(level, x, y, z, random, this.getTreeHeight() - random.nextBoundedInt(3));

        this.placeLeaves(level, topSize, lRadius, x, y, z, random);
    }

    public void placeLeaves(ChunkManager level, int topSize, int lRadius, int x, int y, int z, NukkitRandom random)   {
        int radius = random.nextBoundedInt(2);
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
                        level.setBlockAt(xx, yyy, zz, this.getLeafBlock(), this.getType());
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
