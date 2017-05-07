package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockVine;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

public class NewJungleBigTree extends HugeTreesGenerator {
    public NewJungleBigTree(int baseHeightIn, int extraRandomHeightIn, Block woodMetadataIn, Block leavesMetadataIn) {
        super(baseHeightIn, extraRandomHeightIn, woodMetadataIn, leavesMetadataIn);
    }

    public boolean generate(ChunkManager level, NukkitRandom rand, Vector3 position) {
        int i = this.getHeight(rand);

        if (!this.ensureGrowable(level, rand, position, i)) {
            return false;
        } else {
            this.createCrown(level, position.up(i), 2);

            for (int j = (int) position.getY() + i - 2 - rand.nextBoundedInt(4); j > position.getY() + i / 2; j -= 2 + rand.nextBoundedInt(4)) {
                float f = rand.nextFloat() * ((float) Math.PI * 2F);
                int k = (int) (position.getX() + (0.5F + MathHelper.cos(f) * 4.0F));
                int l = (int) (position.getZ() + (0.5F + MathHelper.sin(f) * 4.0F));

                for (int i1 = 0; i1 < 5; ++i1) {
                    k = (int) (position.getX() + (1.5F + MathHelper.cos(f) * (float) i1));
                    l = (int) (position.getZ() + (1.5F + MathHelper.sin(f) * (float) i1));
                    this.setBlockAndNotifyAdequately(level, new Vector3(k, j - 3 + i1 / 2, l), this.woodMetadata);
                }

                int j2 = 1 + rand.nextBoundedInt(2);
                int j1 = j;

                for (int k1 = j - j2; k1 <= j1; ++k1) {
                    int l1 = k1 - j1;
                    this.growLeavesLayer(level, new Vector3(k, k1, l), 1 - l1);
                }
            }

            for (int i2 = 0; i2 < i; ++i2) {
                Vector3 blockpos = position.up(i2);

                if (this.canGrowInto(level.getBlockIdAt((int) blockpos.x, (int) blockpos.y, (int) blockpos.z))) {
                    this.setBlockAndNotifyAdequately(level, blockpos, this.woodMetadata);

                    if (i2 > 0) {
                        this.placeVine(level, rand, blockpos.west(), 8);
                        this.placeVine(level, rand, blockpos.north(), 1);
                    }
                }

                if (i2 < i - 1) {
                    Vector3 blockpos1 = blockpos.east();

                    if (this.canGrowInto(level.getBlockIdAt((int) blockpos1.x, (int) blockpos1.y, (int) blockpos1.z))) {
                        this.setBlockAndNotifyAdequately(level, blockpos1, this.woodMetadata);

                        if (i2 > 0) {
                            this.placeVine(level, rand, blockpos1.east(), 2);
                            this.placeVine(level, rand, blockpos1.north(), 1);
                        }
                    }

                    Vector3 blockpos2 = blockpos.south().east();

                    if (this.canGrowInto(level.getBlockIdAt((int) blockpos2.x, (int) blockpos2.y, (int) blockpos2.z))) {
                        this.setBlockAndNotifyAdequately(level, blockpos2, this.woodMetadata);

                        if (i2 > 0) {
                            this.placeVine(level, rand, blockpos2.east(), 2);
                            this.placeVine(level, rand, blockpos2.south(), 4);
                        }
                    }

                    Vector3 blockpos3 = blockpos.south();

                    if (this.canGrowInto(level.getBlockIdAt((int) blockpos3.x, (int) blockpos3.y, (int) blockpos3.z))) {
                        this.setBlockAndNotifyAdequately(level, blockpos3, this.woodMetadata);

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

    private void placeVine(ChunkManager level, NukkitRandom random, Vector3 pos, int meta) {
        if (random.nextBoundedInt(3) > 0 && level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z) == 0) {
            this.setBlockAndNotifyAdequately(level, pos, new BlockVine(meta));
        }
    }

    private void createCrown(ChunkManager level, Vector3 pos, int i1) {
        for (int j = -2; j <= 0; ++j) {
            this.growLeavesLayerStrict(level, pos.up(j), i1 + 1 - j);
        }
    }
}
