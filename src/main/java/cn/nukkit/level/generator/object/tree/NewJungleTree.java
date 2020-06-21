package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.*;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

/**
 * Created by CreeperFace on 26. 10. 2016.
 */
public class NewJungleTree extends TreeGenerator {

    /**
     * The minimum height of a generated tree.
     */
    private final int minTreeHeight;

    private final int maxTreeHeight;

    /**
     * The metadata value of the wood to use in tree generation.
     */
    private final Block metaWood = Block.get(BlockID.WOOD, BlockWood.JUNGLE);

    /**
     * The metadata value of the leaves to use in tree generation.
     */
    private final Block metaLeaves = Block.get(BlockID.LEAVES, BlockLeaves.JUNGLE);

    public NewJungleTree(final int minTreeHeight, final int maxTreeHeight) {
        this.minTreeHeight = minTreeHeight;
        this.maxTreeHeight = maxTreeHeight;
    }

    @Override
    public boolean generate(final ChunkManager worldIn, final NukkitRandom rand, final Vector3 vectorPosition) {
        final BlockVector3 position = new BlockVector3(vectorPosition.getFloorX(), vectorPosition.getFloorY(), vectorPosition.getFloorZ());

        final int i = rand.nextBoundedInt(this.maxTreeHeight) + this.minTreeHeight;
        boolean flag = true;

        if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
            for (int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
                int k = 1;

                if (j == position.getY()) {
                    k = 0;
                }

                if (j >= position.getY() + 1 + i - 2) {
                    k = 2;
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

                if ((block == BlockID.GRASS || block == BlockID.DIRT || block == BlockID.FARMLAND) && position.getY() < 256 - i - 1) {
                    this.setDirtAt(worldIn, down);
                    final int k2 = 3;
                    final int l2 = 0;

                    for (int i3 = position.getY() - 3 + i; i3 <= position.getY() + i; ++i3) {
                        final int i4 = i3 - (position.getY() + i);
                        final int j1 = 1 - i4 / 2;

                        for (int k1 = position.getX() - j1; k1 <= position.getX() + j1; ++k1) {
                            final int l1 = k1 - position.getX();

                            for (int i2 = position.getZ() - j1; i2 <= position.getZ() + j1; ++i2) {
                                final int j2 = i2 - position.getZ();

                                if (Math.abs(l1) != j1 || Math.abs(j2) != j1 || rand.nextBoundedInt(2) != 0 && i4 != 0) {
                                    final BlockVector3 blockpos = new BlockVector3(k1, i3, i2);
                                    final int id = worldIn.getBlockIdAt(blockpos.x, blockpos.y, blockpos.z);

                                    if (id == BlockID.AIR || id == BlockID.LEAVES || id == BlockID.VINE) {
                                        this.setBlockAndNotifyAdequately(worldIn, blockpos, this.metaLeaves);
                                    }
                                }
                            }
                        }
                    }

                    for (int j3 = 0; j3 < i; ++j3) {
                        final BlockVector3 up = position.up(j3);
                        final int id = worldIn.getBlockIdAt(up.x, up.y, up.z);

                        if (id == BlockID.AIR || id == BlockID.LEAVES || id == BlockID.VINE) {
                            this.setBlockAndNotifyAdequately(worldIn, up, this.metaWood);

                            if (j3 > 0) {
                                if (rand.nextBoundedInt(3) > 0 && this.isAirBlock(worldIn, position.add(-1, j3, 0))) {
                                    this.addVine(worldIn, position.add(-1, j3, 0), 8);
                                }

                                if (rand.nextBoundedInt(3) > 0 && this.isAirBlock(worldIn, position.add(1, j3, 0))) {
                                    this.addVine(worldIn, position.add(1, j3, 0), 2);
                                }

                                if (rand.nextBoundedInt(3) > 0 && this.isAirBlock(worldIn, position.add(0, j3, -1))) {
                                    this.addVine(worldIn, position.add(0, j3, -1), 1);
                                }

                                if (rand.nextBoundedInt(3) > 0 && this.isAirBlock(worldIn, position.add(0, j3, 1))) {
                                    this.addVine(worldIn, position.add(0, j3, 1), 4);
                                }
                            }
                        }
                    }

                    for (int k3 = position.getY() - 3 + i; k3 <= position.getY() + i; ++k3) {
                        final int j4 = k3 - (position.getY() + i);
                        final int k4 = 2 - j4 / 2;
                        final BlockVector3 pos2 = new BlockVector3();

                        for (int l4 = position.getX() - k4; l4 <= position.getX() + k4; ++l4) {
                            for (int i5 = position.getZ() - k4; i5 <= position.getZ() + k4; ++i5) {
                                pos2.setComponents(l4, k3, i5);

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

                    if (rand.nextBoundedInt(5) == 0 && i > 5) {
                        for (int l3 = 0; l3 < 2; ++l3) {
                            for (final BlockFace enumfacing : BlockFace.Plane.HORIZONTAL) {
                                if (rand.nextBoundedInt(4 - l3) == 0) {
                                    final BlockFace enumfacing1 = enumfacing.getOpposite();
                                    this.placeCocoa(worldIn, rand.nextBoundedInt(3), position.add(enumfacing1.getXOffset(), i - 5 + l3, enumfacing1.getZOffset()), enumfacing);
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

    private void placeCocoa(final ChunkManager worldIn, final int age, final BlockVector3 pos, final BlockFace side) {
        final int meta = this.getCocoaMeta(age, side.getIndex());

        this.setBlockAndNotifyAdequately(worldIn, pos, new BlockUnknown(127, meta));
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

    private boolean isAirBlock(final ChunkManager level, final BlockVector3 v) {
        return level.getBlockIdAt(v.x, v.y, v.z) == 0;
    }

    private int getCocoaMeta(final int age, final int side) {
        int meta = 0;

        meta *= age;

        //3 4 2 5
        switch (side) {
            case 4:
                meta++;
                break;
            case 2:
                meta += 2;
                break;
            case 5:
                meta += 3;
                break;
        }

        return meta;
    }

}
