package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

public abstract class HugeTreesGenerator extends TreeGenerator {
    /**
     * The base height of the tree
     */
    protected final int baseHeight;

    /**
     * Sets the metadata for the wood blocks used
     */
    protected final Block woodMetadata;

    /**
     * Sets the metadata for the leaves used in huge trees
     */
    protected final Block leavesMetadata;
    protected int extraRandomHeight;

    public HugeTreesGenerator(int baseHeightIn, int extraRandomHeightIn, Block woodMetadataIn, Block leavesMetadataIn) {
        this.baseHeight = baseHeightIn;
        this.extraRandomHeight = extraRandomHeightIn;
        this.woodMetadata = woodMetadataIn;
        this.leavesMetadata = leavesMetadataIn;
    }

    /**
     * calculates the height based on this trees base height and its extra random height
     */
    protected int getHeight(NukkitRandom rand) {
        int i = rand.nextBoundedInt(3) + this.baseHeight;

        if (this.extraRandomHeight > 1) {
            i += rand.nextBoundedInt(this.extraRandomHeight);
        }

        return i;
    }

    /**
     * returns whether or not there is space for a tree to grow at a certain position
     */
    private boolean isSpaceAt(ChunkManager worldIn, Vector3 leavesPos, int height) {
        boolean flag = true;

        if (leavesPos.getY() >= 1 && leavesPos.getY() + height + 1 <= 256) {
            for (int i = 0; i <= 1 + height; ++i) {
                int j = 2;

                if (i == 0) {
                    j = 1;
                } else if (i >= 1 + height - 2) {
                    j = 2;
                }

                for (int k = -j; k <= j && flag; ++k) {
                    for (int l = -j; l <= j && flag; ++l) {
                        Vector3 blockPos = leavesPos.add(k, i, l);
                        if (leavesPos.getY() + i < 0 || leavesPos.getY() + i >= 256 || !this.canGrowInto(worldIn.getBlockIdAt((int) blockPos.x, (int) blockPos.y, (int) blockPos.z))) {
                            flag = false;
                        }
                    }
                }
            }

            return flag;
        } else {
            return false;
        }
    }

    /**
     * returns whether or not there is dirt underneath the block where the tree will be grown.
     * It also generates dirt around the block in a 2x2 square if there is dirt underneath the blockpos.
     */
    private boolean ensureDirtsUnderneath(Vector3 pos, ChunkManager worldIn) {
        Vector3 blockpos = pos.down();
        int block = worldIn.getBlockIdAt((int) blockpos.x, (int) blockpos.y, (int) blockpos.z);

        if ((block == Block.GRASS || block == Block.DIRT) && pos.getY() >= 2) {
            this.setDirtAt(worldIn, blockpos);
            this.setDirtAt(worldIn, blockpos.east());
            this.setDirtAt(worldIn, blockpos.south());
            this.setDirtAt(worldIn, blockpos.south().east());
            return true;
        } else {
            return false;
        }
    }

    /**
     * returns whether or not a tree can grow at a specific position.
     * If it can, it generates surrounding dirt underneath.
     */
    protected boolean ensureGrowable(ChunkManager worldIn, NukkitRandom rand, Vector3 treePos, int p_175929_4_) {
        return this.isSpaceAt(worldIn, treePos, p_175929_4_) && this.ensureDirtsUnderneath(treePos, worldIn);
    }

    /**
     * grow leaves in a circle with the outsides being within the circle
     */
    protected void growLeavesLayerStrict(ChunkManager worldIn, Vector3 layerCenter, int width) {
        int i = width * width;

        for (int j = -width; j <= width + 1; ++j) {
            for (int k = -width; k <= width + 1; ++k) {
                int l = j - 1;
                int i1 = k - 1;

                if (j * j + k * k <= i || l * l + i1 * i1 <= i || j * j + i1 * i1 <= i || l * l + k * k <= i) {
                    Vector3 blockpos = layerCenter.add(j, 0, k);
                    int id = worldIn.getBlockIdAt((int) blockpos.x, (int) blockpos.y, (int) blockpos.z);

                    if (id == Block.AIR || id == Block.LEAVES) {
                        this.setBlockAndNotifyAdequately(worldIn, blockpos, this.leavesMetadata);
                    }
                }
            }
        }
    }

    /**
     * grow leaves in a circle
     */
    protected void growLeavesLayer(ChunkManager worldIn, Vector3 layerCenter, int width) {
        int i = width * width;

        for (int j = -width; j <= width; ++j) {
            for (int k = -width; k <= width; ++k) {
                if (j * j + k * k <= i) {
                    Vector3 blockpos = layerCenter.add(j, 0, k);
                    int id = worldIn.getBlockIdAt((int) blockpos.x, (int) blockpos.y, (int) blockpos.z);

                    if (id == Block.AIR || id == Block.LEAVES) {
                        this.setBlockAndNotifyAdequately(worldIn, blockpos, this.leavesMetadata);
                    }
                }
            }
        }
    }
}

