package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockWood;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class ObjectBigSpruceTree extends ObjectSpruceTree {
    private final float leafStartHeightMultiplier;
    private final int baseLeafRadius;

    public ObjectBigSpruceTree(float leafStartHeightMultiplier, int baseLeafRadius) {
        this.leafStartHeightMultiplier = leafStartHeightMultiplier;
        this.baseLeafRadius = baseLeafRadius;
    }

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        this.treeHeight = random.nextBoundedInt(15) + 20;

        int topSize = this.treeHeight - (int) (this.treeHeight * leafStartHeightMultiplier);
        int lRadius = baseLeafRadius + random.nextBoundedInt(2);

        this.placeTrunk(level, x, y, z, random, this.getTreeHeight() - random.nextBoundedInt(3));

        this.placeLeaves(level, topSize, lRadius, x, y, z, random);
    }

    @Override
    protected void placeTrunk(ChunkManager level, int x, int y, int z, NukkitRandom random, int trunkHeight) {
        // The base dirt block
        level.setBlockIdAt(x, y - 1, z, Block.DIRT);
        int radius = 2;

        for (int yy = 0; yy < trunkHeight; ++yy) {
            for (int xx = 0; xx < radius; xx++) {
                for (int zz = 0; zz < radius; zz++) {
                    int blockId = level.getBlockIdAt(x, y + yy, z);
                    if (this.overridable(blockId)) {
                        level.setBlockFullIdAt(x + xx, y + yy, z + zz, (this.getTrunkBlock() << 4) | this.getType());
                    }
                }
            }
        }
    }
}
