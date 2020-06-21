package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class ObjectBigSpruceTree extends ObjectSpruceTree {

    private final float leafStartHeightMultiplier;

    private final int baseLeafRadius;

    public ObjectBigSpruceTree(final float leafStartHeightMultiplier, final int baseLeafRadius) {
        this.leafStartHeightMultiplier = leafStartHeightMultiplier;
        this.baseLeafRadius = baseLeafRadius;
    }

    @Override
    public void placeObject(final ChunkManager level, final int x, final int y, final int z, final NukkitRandom random) {
        this.treeHeight = random.nextBoundedInt(15) + 20;

        final int topSize = this.treeHeight - (int) (this.treeHeight * this.leafStartHeightMultiplier);
        final int lRadius = this.baseLeafRadius + random.nextBoundedInt(2);

        this.placeTrunk(level, x, y, z, random, this.getTreeHeight() - random.nextBoundedInt(3));

        this.placeLeaves(level, topSize, lRadius, x, y, z, random);
    }

    @Override
    protected void placeTrunk(final ChunkManager level, final int x, final int y, final int z, final NukkitRandom random, final int trunkHeight) {
        // The base dirt block
        level.setBlockAt(x, y - 1, z, BlockID.DIRT);
        final int radius = 2;

        for (int yy = 0; yy < trunkHeight; ++yy) {
            for (int xx = 0; xx < radius; xx++) {
                for (int zz = 0; zz < radius; zz++) {
                    final int blockId = level.getBlockIdAt(x, y + yy, z);
                    if (this.overridable(blockId)) {
                        level.setBlockAt(x + xx, y + yy, z + zz, this.getTrunkBlock(), this.getType());
                    }
                }
            }
        }
    }

}
