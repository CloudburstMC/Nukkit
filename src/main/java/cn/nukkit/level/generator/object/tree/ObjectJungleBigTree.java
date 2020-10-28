package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.NukkitRandom;

import java.util.List;

public class ObjectJungleBigTree extends HugeTreesGenerator {
    public ObjectJungleBigTree(int baseHeightIn, int extraRandomHeight, Block woodMetadata, Block leavesMetadata) {
        super(baseHeightIn, extraRandomHeight, woodMetadata, leavesMetadata);
    }

    public boolean generate(ChunkManager level, List<Block> blocks, NukkitRandom rand, BlockVector3 position) {
        int height = this.getHeight(rand);

        if (!this.ensureGrowable(level, blocks, rand, position, height)) {
            return false;
        } else {
            this.createCrown(level, blocks, position.up(height), 2);

            for (int j = position.getY() + height - 2 - rand.nextBoundedInt(4); j > position.getY() + height / 2; j -= 2 + rand.nextBoundedInt(4)) {
                float f = rand.nextFloat() * ((float) Math.PI * 2F);
                int k = (int) (position.getX() + (0.5F + MathHelper.cos(f) * 4.0F));
                int l = (int) (position.getZ() + (0.5F + MathHelper.sin(f) * 4.0F));

                for (int i1 = 0; i1 < 5; ++i1) {
                    k = (int) (position.getX() + (1.5F + MathHelper.cos(f) * (float) i1));
                    l = (int) (position.getZ() + (1.5F + MathHelper.sin(f) * (float) i1));
                    this.setBlockAndNotifyAdequately(blocks, new BlockVector3(k, j - 3 + i1 / 2, l), this.woodMetadata);
                }

                int j2 = 1 + rand.nextBoundedInt(2);

                for (int k1 = j - j2; k1 <= j; ++k1) {
                    int l1 = k1 - j;
                    this.growLeavesLayer(level, blocks, new BlockVector3(k, k1, l), 1 - l1);
                }
            }

            for (int i2 = 0; i2 < height; ++i2) {
                BlockVector3 blockpos = position.up(i2);

                if (this.canGrowInto(level.getBlockIdAt(blockpos.getX(), blockpos.getY(), blockpos.getZ()))) {
                    this.setBlockAndNotifyAdequately(blocks, blockpos, this.woodMetadata);

                    if (i2 > 0) {
                        this.placeVine(level, blocks, rand, blockpos.west(), 8);
                        this.placeVine(level, blocks, rand, blockpos.north(), 1);
                    }
                }

                if (i2 < height - 1) {
                    BlockVector3 blockpos1 = blockpos.east();

                    if (this.canGrowInto(level.getBlockIdAt(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ()))) {
                        this.setBlockAndNotifyAdequately(blocks, blockpos1, this.woodMetadata);

                        if (i2 > 0) {
                            this.placeVine(level, blocks, rand, blockpos1.east(), 2);
                            this.placeVine(level, blocks, rand, blockpos1.north(), 1);
                        }
                    }

                    BlockVector3 blockpos2 = blockpos.south().east();

                    if (this.canGrowInto(level.getBlockIdAt(blockpos2.getX(), blockpos2.getY(), blockpos2.getZ()))) {
                        this.setBlockAndNotifyAdequately(blocks, blockpos2, this.woodMetadata);

                        if (i2 > 0) {
                            this.placeVine(level, blocks, rand, blockpos2.east(), 2);
                            this.placeVine(level, blocks, rand, blockpos2.south(), 4);
                        }
                    }

                    BlockVector3 blockpos3 = blockpos.south();

                    if (this.canGrowInto(level.getBlockIdAt(blockpos3.getX(), blockpos3.getY(), blockpos3.getZ()))) {
                        this.setBlockAndNotifyAdequately(blocks, blockpos3, this.woodMetadata);

                        if (i2 > 0) {
                            this.placeVine(level, blocks, rand, blockpos3.west(), 8);
                            this.placeVine(level, blocks, rand, blockpos3.south(), 4);
                        }
                    }
                }
            }

            return true;
        }
    }

    private void placeVine(ChunkManager level, List<Block> blocks, NukkitRandom random, BlockVector3 pos, int meta) {
        if (random.nextBoundedInt(3) > 0 && level.getBlockIdAt(pos.getX(), pos.getY(), pos.getZ()) == 0) {
            this.setBlockAndNotifyAdequately(blocks, pos, Block.get(BlockID.VINE, meta));
        }
    }

    private void createCrown(ChunkManager level, List<Block> blocks, BlockVector3 pos, int i1) {
        for (int j = -2; j <= 0; ++j) {
            this.growLeavesLayerStrict(level, blocks, pos.up(j), i1 + 1 - j);
        }
    }
}
