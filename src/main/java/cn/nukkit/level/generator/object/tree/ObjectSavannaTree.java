package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLeaves2;
import cn.nukkit.block.BlockWood2;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.object.BlockList;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import java.util.List;

public class ObjectSavannaTree extends TreeGenerator {
    private static final Block TRUNK = Block.get(BlockID.WOOD2, BlockWood2.ACACIA);
    private static final Block LEAF = Block.get(BlockID.LEAVES2, BlockLeaves2.ACACIA);

    @Override
    public boolean generate(ChunkManager level, List<Block> blocks, NukkitRandom rand, BlockVector3 position) {
        int i = rand.nextBoundedInt(3) + rand.nextBoundedInt(3) + 5;
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

                BlockVector3 blockVector3 = new BlockVector3();

                for (int l = position.getX() - k; l <= position.getX() + k && flag; ++l) {
                    for (int i1 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
                        if (j >= 0 && j < 256) {

                            blockVector3.setComponents(l, j, i1);
                            if (!this.canGrowInto(level.getBlockIdAt(blockVector3.getX(), blockVector3.getY(), blockVector3.getZ()))) {
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
                BlockVector3 down = position.down();
                int block = level.getBlockIdAt(down.getX(), down.getY(), down.getZ());

                if ((block == Block.GRASS || block == Block.DIRT) && position.getY() < 256 - i - 1) {
                    this.setDirtAt(level, blocks, position.down());
                    BlockFace face = BlockFace.Plane.HORIZONTAL.random(rand);
                    int k2 = i - rand.nextBoundedInt(4) - 1;
                    int l2 = 3 - rand.nextBoundedInt(3);
                    int i3 = position.getX();
                    int j1 = position.getZ();
                    int k1 = 0;

                    for (int l1 = 0; l1 < i; ++l1) {
                        int i2 = position.getY() + l1;

                        if (l1 >= k2 && l2 > 0) {
                            i3 += face.getXOffset();
                            j1 += face.getZOffset();
                            --l2;
                        }

                        BlockVector3 blockpos = new BlockVector3(i3, i2, j1);
                        int material = level.getBlockIdAt(blockpos.getX(), blockpos.getY(), blockpos.getZ());

                        if (material == Block.AIR || material == Block.LEAVES) {
                            this.placeLogAt(blocks, blockpos);
                            k1 = i2;
                        }
                    }

                    BlockVector3 blockpos2 = new BlockVector3(i3, k1, j1);

                    for (int j3 = -3; j3 <= 3; ++j3) {
                        for (int i4 = -3; i4 <= 3; ++i4) {
                            if (Math.abs(j3) != 3 || Math.abs(i4) != 3) {
                                this.placeLeafAt(level, blocks, blockpos2.add(j3, 0, i4));
                            }
                        }
                    }

                    blockpos2 = blockpos2.up();

                    for (int k3 = -1; k3 <= 1; ++k3) {
                        for (int j4 = -1; j4 <= 1; ++j4) {
                            this.placeLeafAt(level, blocks, blockpos2.add(k3, 0, j4));
                        }
                    }

                    this.placeLeafAt(level, blocks, blockpos2.east(2));
                    this.placeLeafAt(level, blocks, blockpos2.west(2));
                    this.placeLeafAt(level, blocks, blockpos2.south(2));
                    this.placeLeafAt(level, blocks, blockpos2.north(2));
                    i3 = position.getX();
                    j1 = position.getZ();
                    BlockFace face1 = BlockFace.Plane.HORIZONTAL.random(rand);

                    if (face1 != face) {
                        int l3 = k2 - rand.nextBoundedInt(2) - 1;
                        int k4 = 1 + rand.nextBoundedInt(3);
                        k1 = 0;

                        for (int l4 = l3; l4 < i && k4 > 0; --k4) {
                            if (l4 >= 1) {
                                int j2 = position.getY() + l4;
                                i3 += face1.getXOffset();
                                j1 += face1.getZOffset();
                                BlockVector3 blockpos1 = new BlockVector3(i3, j2, j1);
                                int material1 = level.getBlockIdAt(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());

                                if (material1 == Block.AIR || material1 == Block.LEAVES) {
                                    this.placeLogAt(blocks, blockpos1);
                                    k1 = j2;
                                }
                            }

                            ++l4;
                        }

                        if (k1 > 0) {
                            BlockVector3 blockpos3 = new BlockVector3(i3, k1, j1);

                            for (int i5 = -2; i5 <= 2; ++i5) {
                                for (int k5 = -2; k5 <= 2; ++k5) {
                                    if (Math.abs(i5) != 2 || Math.abs(k5) != 2) {
                                        this.placeLeafAt(level, blocks, blockpos3.add(i5, 0, k5));
                                    }
                                }
                            }

                            blockpos3 = blockpos3.up();

                            for (int j5 = -1; j5 <= 1; ++j5) {
                                for (int l5 = -1; l5 <= 1; ++l5) {
                                    this.placeLeafAt(level, blocks, blockpos3.add(j5, 0, l5));
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

    @Deprecated
    public boolean generate(ChunkManager level, NukkitRandom rand, Vector3 position) {
        return this.generate(level, new BlockList(level), rand, position.asBlockVector3());
    }

    private void placeLogAt(List<Block> blocks, BlockVector3 pos) {
        this.setBlockAndNotifyAdequately(blocks, pos, TRUNK);
    }

    private void placeLeafAt(ChunkManager worldIn, List<Block> blocks, BlockVector3 pos) {
        int material = worldIn.getBlockIdAt(pos.getX(), pos.getY(), pos.getZ());

        if (material == Block.AIR || material == Block.LEAVES) {
            this.setBlockAndNotifyAdequately(blocks, pos, LEAF);
        }
    }
}
