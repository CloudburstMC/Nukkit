package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.*;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

public class NewSwampTree extends TreeGenerator {
    
    /**
     * The metadata value of the wood to use in tree generation.
     */
    private final Block metaWood = new BlockWood(BlockWood.OAK);

    /**
     * The metadata value of the leaves to use in tree generation.
     */
    private final Block metaLeaves = new BlockLeaves(BlockLeaves.OAK);

    @Override
    public boolean generate(ChunkManager worldIn, NukkitRandom rand, Vector3 vectorPosition) {
        BlockVector3 position = new BlockVector3(vectorPosition.getFloorX(), vectorPosition.getFloorY(), vectorPosition.getFloorZ());

        int i = rand.nextBoundedInt(4) + 5;
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

                BlockVector3 pos2 = new BlockVector3();

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
                BlockVector3 down = position.getSide(Vector3.SIDE_DOWN);
                int block = worldIn.getBlockIdAt(down.x, down.y, down.z);

                if ((block == Block.GRASS || block == Block.DIRT) && position.getY() < 256 - i - 1) {
                    this.setDirtAt(worldIn, down);

                    for (int k1 = position.getY() - 3 + i; k1 <= position.getY() + i; ++k1) {
                        int j2 = k1 - (position.getY() + i);
                        int l2 = 2 - j2 / 2;

                        for (int j3 = position.getX() - l2; j3 <= position.getX() + l2; ++j3) {
                            int k3 = j3 - position.getX();

                            for (int i4 = position.getZ() - l2; i4 <= position.getZ() + l2; ++i4) {
                                int j1 = i4 - position.getZ();

                                if (Math.abs(k3) != l2 || Math.abs(j1) != l2 || rand.nextBoundedInt(2) != 0 && j2 != 0) {
                                    BlockVector3 blockpos = new BlockVector3(j3, k1, i4);
                                    int id = worldIn.getBlockIdAt(blockpos.x, blockpos.y, blockpos.z);

                                    if (id == Block.AIR || id == Block.LEAVES || id == Block.VINE) {
                                        this.setBlockAndNotifyAdequately(worldIn, blockpos, this.metaLeaves);
                                    }
                                }
                            }
                        }
                    }

                    for (int l1 = 0; l1 < i; ++l1) {
                        BlockVector3 up = position.getSide(Vector3.SIDE_UP, l1);
                        int id = worldIn.getBlockIdAt(up.x, up.y, up.z);

                        if (id == Block.AIR || id == Block.LEAVES || id == Block.WATER || id == Block.STILL_WATER) {
                            this.setBlockAndNotifyAdequately(worldIn, up, this.metaWood);
                        }
                    }

                    for (int i2 = position.getY() - 3 + i; i2 <= position.getY() + i; ++i2) {
                        int k2 = i2 - (position.getY() + i);
                        int i3 = 2 - k2 / 2;
                        BlockVector3 pos2 = new BlockVector3();

                        for (int l3 = position.getX() - i3; l3 <= position.getX() + i3; ++l3) {
                            for (int j4 = position.getZ() - i3; j4 <= position.getZ() + i3; ++j4) {
                                pos2.setComponents(l3, i2, j4);

                                if (worldIn.getBlockIdAt(pos2.x, pos2.y, pos2.z) == Block.LEAVES) {
                                    BlockVector3 blockpos2 = pos2.getSide(Vector3.SIDE_WEST);
                                    BlockVector3 blockpos3 = pos2.getSide(BlockVector3.SIDE_EAST);
                                    BlockVector3 blockpos4 = pos2.getSide(BlockVector3.SIDE_NORTH);
                                    BlockVector3 blockpos1 = pos2.getSide(BlockVector3.SIDE_SOUTH);

                                    if (rand.nextBoundedInt(4) == 0 && worldIn.getBlockIdAt(blockpos2.x, blockpos2.y, blockpos2.z) == Block.AIR) {
                                        this.addHangingVine(worldIn, blockpos2, 8);
                                    }

                                    if (rand.nextBoundedInt(4) == 0 && worldIn.getBlockIdAt(blockpos3.x, blockpos3.y, blockpos3.z) == Block.AIR) {
                                        this.addHangingVine(worldIn, blockpos3, 2);
                                    }

                                    if (rand.nextBoundedInt(4) == 0 && worldIn.getBlockIdAt(blockpos4.x, blockpos4.y, blockpos4.z) == Block.AIR) {
                                        this.addHangingVine(worldIn, blockpos4, 1);
                                    }

                                    if (rand.nextBoundedInt(4) == 0 && worldIn.getBlockIdAt(blockpos1.x, blockpos1.y, blockpos1.z) == Block.AIR) {
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

    private void addVine(ChunkManager worldIn, BlockVector3 pos, int meta) {
        this.setBlockAndNotifyAdequately(worldIn, pos, new BlockVine(meta));
    }

    private void addHangingVine(ChunkManager worldIn, BlockVector3 pos, int meta) {
        this.addVine(worldIn, pos, meta);
        int i = 4;

        for (pos = pos.getSide(BlockVector3.SIDE_DOWN); i > 0 && worldIn.getBlockIdAt(pos.x, pos.y, pos.z) == Block.AIR; --i) {
            this.addVine(worldIn, pos, meta);
            pos = pos.getSide(BlockVector3.SIDE_DOWN);
        }
    }
}
