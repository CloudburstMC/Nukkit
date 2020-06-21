package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLeaves;
import cn.nukkit.block.BlockWood;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

public class ObjectSwampTree extends TreeGenerator {

    /**
     * The metadata value of the wood to use in tree generation.
     */
    private final Block metaWood = Block.get(BlockID.WOOD, BlockWood.OAK);

    /**
     * The metadata value of the leaves to use in tree generation.
     */
    private final Block metaLeaves = Block.get(BlockID.LEAVES, BlockLeaves.OAK);

    @Override
    public boolean generate(final ChunkManager worldIn, final NukkitRandom rand, final Vector3 vectorPosition) {
        final BlockVector3 position = new BlockVector3(vectorPosition.getFloorX(), vectorPosition.getFloorY(), vectorPosition.getFloorZ());

        final int i = rand.nextBoundedInt(4) + 5;
        boolean flag = true;

        if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
            for (int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
                int k = 1;

                if (j == position.getY()) {
                    k = 0;
                }

                if (j >= position.getY() + 1 + i - 2) {
                    k = 3;
                }

                final BlockVector3 pos2 = new BlockVector3();

                for (int l = position.getX() - k; l <= position.getX() + k && flag; ++l) {
                    for (int i1 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
                        if (j >= 0 && j < 256) {
                            pos2.setComponents(l, j, i1);
                            if (!this.canGrowInto(worldIn.getBlockIdAt(pos2.x, pos2.y, pos2.z))) {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag) {
                return false;
            } else {
                final BlockVector3 down = position.down();
                final int block = worldIn.getBlockIdAt(down.x, down.y, down.z);

                if ((block == BlockID.GRASS || block == BlockID.DIRT) && position.getY() < 256 - i - 1) {
                    this.setDirtAt(worldIn, down);

                    for (int k1 = position.getY() - 3 + i; k1 <= position.getY() + i; ++k1) {
                        final int j2 = k1 - (position.getY() + i);
                        final int l2 = 2 - j2 / 2;

                        for (int j3 = position.getX() - l2; j3 <= position.getX() + l2; ++j3) {
                            final int k3 = j3 - position.getX();

                            for (int i4 = position.getZ() - l2; i4 <= position.getZ() + l2; ++i4) {
                                final int j1 = i4 - position.getZ();

                                if (Math.abs(k3) != l2 || Math.abs(j1) != l2 || rand.nextBoundedInt(2) != 0 && j2 != 0) {
                                    final BlockVector3 blockpos = new BlockVector3(j3, k1, i4);
                                    final int id = worldIn.getBlockIdAt(blockpos.x, blockpos.y, blockpos.z);

                                    if (id == BlockID.AIR || id == BlockID.LEAVES || id == BlockID.VINE) {
                                        this.setBlockAndNotifyAdequately(worldIn, blockpos, this.metaLeaves);
                                    }
                                }
                            }
                        }
                    }

                    for (int l1 = 0; l1 < i; ++l1) {
                        final BlockVector3 up = position.up(l1);
                        final int id = worldIn.getBlockIdAt(up.x, up.y, up.z);

                        if (id == BlockID.AIR || id == BlockID.LEAVES || id == BlockID.WATER || id == BlockID.STILL_WATER) {
                            this.setBlockAndNotifyAdequately(worldIn, up, this.metaWood);
                        }
                    }

                    for (int i2 = position.getY() - 3 + i; i2 <= position.getY() + i; ++i2) {
                        final int k2 = i2 - (position.getY() + i);
                        final int i3 = 2 - k2 / 2;
                        final BlockVector3 pos2 = new BlockVector3();

                        for (int l3 = position.getX() - i3; l3 <= position.getX() + i3; ++l3) {
                            for (int j4 = position.getZ() - i3; j4 <= position.getZ() + i3; ++j4) {
                                pos2.setComponents(l3, i2, j4);

                                if (worldIn.getBlockIdAt(pos2.x, pos2.y, pos2.z) == BlockID.LEAVES) {
                                    final BlockVector3 blockpos2 = pos2.west();
                                    final BlockVector3 blockpos3 = pos2.east();
                                    final BlockVector3 blockpos4 = pos2.north();
                                    final BlockVector3 blockpos1 = pos2.south();

                                    if (rand.nextBoundedInt(4) == 0 && worldIn.getBlockIdAt(blockpos2.x, blockpos2.y, blockpos2.z) == BlockID.AIR) {
                                        this.addHangingVine(worldIn, blockpos2, 8);
                                    }

                                    if (rand.nextBoundedInt(4) == 0 && worldIn.getBlockIdAt(blockpos3.x, blockpos3.y, blockpos3.z) == BlockID.AIR) {
                                        this.addHangingVine(worldIn, blockpos3, 2);
                                    }

                                    if (rand.nextBoundedInt(4) == 0 && worldIn.getBlockIdAt(blockpos4.x, blockpos4.y, blockpos4.z) == BlockID.AIR) {
                                        this.addHangingVine(worldIn, blockpos4, 1);
                                    }

                                    if (rand.nextBoundedInt(4) == 0 && worldIn.getBlockIdAt(blockpos1.x, blockpos1.y, blockpos1.z) == BlockID.AIR) {
                                        this.addHangingVine(worldIn, blockpos1, 4);
                                    }
                                }
                            }
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    private void addVine(final ChunkManager worldIn, final BlockVector3 pos, final int meta) {
        this.setBlockAndNotifyAdequately(worldIn, pos, Block.get(BlockID.VINE, meta));
    }

    private void addHangingVine(final ChunkManager worldIn, BlockVector3 pos, final int meta) {
        this.addVine(worldIn, pos, meta);
        int i = 4;

        for (pos = pos.down(); i > 0 && worldIn.getBlockIdAt(pos.x, pos.y, pos.z) == BlockID.AIR; --i) {
            this.addVine(worldIn, pos, meta);
            pos = pos.down();
        }
    }

}
