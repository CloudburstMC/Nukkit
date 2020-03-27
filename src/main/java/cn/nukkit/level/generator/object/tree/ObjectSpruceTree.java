package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.BlockLog;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BedrockRandom;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.LEAVES;
import static cn.nukkit.block.BlockIds.LOG;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ObjectSpruceTree extends ObjectTree {
    protected int treeHeight;

    @Override
    public Identifier getTrunkBlock() {
        return LOG;
    }

    @Override
    public Identifier getLeafBlock() {
        return LEAVES;
    }

    @Override
    public int getType() {
        return BlockLog.SPRUCE;
    }

    @Override
    public int getTreeHeight() {
        return this.treeHeight;
    }

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, BedrockRandom random) {
        this.treeHeight = random.nextInt(4) + 6;

        int topSize = this.getTreeHeight() - (1 + random.nextInt(2));
        int lRadius = 2 + random.nextInt(2);

        this.placeTrunk(level, x, y, z, random, this.getTreeHeight() - random.nextInt(3));

        this.placeLeaves(level, topSize, lRadius, x, y, z, random);
    }

    public void placeLeaves(ChunkManager level, int topSize, int lRadius, int x, int y, int z, BedrockRandom random) {
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

                    if (!BlockRegistry.get().getBlock(level.getBlockId(xx, yyy, zz), 0).isSolid()) {
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
