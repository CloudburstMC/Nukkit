package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BedrockRandom;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.Vector3i;

import static cn.nukkit.block.BlockIds.AIR;

public class ObjectJungleBigTree extends HugeTreesGenerator {
    public ObjectJungleBigTree(int baseHeightIn, int extraRandomHeight, Block woodMetadata, Block leavesMetadata) {
        super(baseHeightIn, extraRandomHeight, woodMetadata, leavesMetadata);
    }

    public boolean generate(ChunkManager level, BedrockRandom rand, Vector3i position) {
        int height = this.getHeight(rand);

        if (!this.ensureGrowable(level, rand, position, height)) {
            return false;
        } else {
            this.createCrown(level, position.up(height), 2);

            for (int j = (int) position.getY() + height - 2 - rand.nextInt(4); j > position.getY() + height / 2; j -= 2 + rand.nextInt(4)) {
                float f = rand.nextFloat() * ((float) Math.PI * 2F);
                int k = (int) (position.getX() + (0.5F + MathHelper.cos(f) * 4.0F));
                int l = (int) (position.getZ() + (0.5F + MathHelper.sin(f) * 4.0F));

                for (int i1 = 0; i1 < 5; ++i1) {
                    k = (int) (position.getX() + (1.5F + MathHelper.cos(f) * (float) i1));
                    l = (int) (position.getZ() + (1.5F + MathHelper.sin(f) * (float) i1));
                    level.setBlockAt(k, j - 3 + i1 / 2, l, this.woodMetadata);
                }

                int j2 = 1 + rand.nextInt(2);

                for (int k1 = j - j2; k1 <= j; ++k1) {
                    int l1 = k1 - j;
                    this.growLeavesLayer(level, new Vector3i(k, k1, l), 1 - l1);
                }
            }

            for (int i2 = 0; i2 < height; ++i2) {
                Vector3i blockpos = position.up(i2);

                if (this.canGrowInto(level.getBlockIdAt(blockpos.x, blockpos.y, blockpos.z))) {
                    level.setBlockAt(blockpos.x, blockpos.y, blockpos.z, this.woodMetadata);

                    if (i2 > 0) {
                        this.placeVine(level, rand, blockpos.west(), 8);
                        this.placeVine(level, rand, blockpos.north(), 1);
                    }
                }

                if (i2 < height - 1) {
                    Vector3i blockpos1 = blockpos.east();

                    if (this.canGrowInto(level.getBlockIdAt(blockpos1.x, blockpos1.y, blockpos1.z))) {
                        level.setBlockAt(blockpos1.x, blockpos1.y, blockpos1.z, this.woodMetadata);

                        if (i2 > 0) {
                            this.placeVine(level, rand, blockpos1.east(), 2);
                            this.placeVine(level, rand, blockpos1.north(), 1);
                        }
                    }

                    Vector3i blockpos2 = blockpos.south().east();

                    if (this.canGrowInto(level.getBlockIdAt(blockpos2.x, blockpos2.y, blockpos2.z))) {
                        level.setBlockAt(blockpos2.x, blockpos2.y, blockpos2.z, this.woodMetadata);

                        if (i2 > 0) {
                            this.placeVine(level, rand, blockpos2.east(), 2);
                            this.placeVine(level, rand, blockpos2.south(), 4);
                        }
                    }

                    Vector3i blockpos3 = blockpos.south();

                    if (this.canGrowInto(level.getBlockIdAt(blockpos3.x, blockpos3.y, blockpos3.z))) {
                        level.setBlockAt(blockpos3.x, blockpos3.y, blockpos3.z, this.woodMetadata);

                        if (i2 > 0) {
                            this.placeVine(level, rand, blockpos3.west(), 8);
                            this.placeVine(level, rand, blockpos3.south(), 4);
                        }
                    }
                }
            }

            return true;
        }
    }

    private void placeVine(ChunkManager level, BedrockRandom random, Vector3i pos, int meta) {
        if (random.nextInt(3) > 0 && level.getBlockIdAt(pos.x, pos.y, pos.z) == AIR) {
            level.setBlockAt(pos.x, pos.y, pos.z, Block.get(BlockIds.VINE, meta));
        }
    }

    private void createCrown(ChunkManager level, Vector3i pos, int i1) {
        for (int j = -2; j <= 0; ++j) {
            this.growLeavesLayerStrict(level, pos.up(j), i1 + 1 - j);
        }
    }
}
