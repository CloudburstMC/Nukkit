package cn.nukkit.server.level.generator.object.tree;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.block.BlockLeaves2;
import cn.nukkit.server.block.BlockWood2;
import cn.nukkit.server.level.ChunkManager;
import cn.nukkit.server.math.BlockFace;
import cn.nukkit.server.math.NukkitRandom;
import cn.nukkit.server.math.Vector3;

public class ObjectSavannaTree extends TreeGenerator {
    private static final Block TRUNK = new BlockWood2(BlockWood2.ACACIA);
    private static final Block LEAF = new BlockLeaves2(BlockLeaves2.ACACIA);

    public boolean generate(ChunkManager level, NukkitRandom rand, Vector3 position) {
        int i = rand.nextBoundedInt(3) + rand.nextBoundedInt(3) + 5;
        boolean flag = true;

        if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
            for (int j = (int) position.getY(); j <= position.getY() + 1 + i; ++j) {
                int k = 1;

                if (j == position.getY()) {
                    k = 0;
                }

                if (j >= position.getY() + 1 + i - 2) {
                    k = 2;
                }

                Vector3 vector3 = new Vector3();

                for (int l = (int) position.getX() - k; l <= position.getX() + k && flag; ++l) {
                    for (int i1 = (int) position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
                        if (j >= 0 && j < 256) {

                            vector3.setComponents(l, j, i1);
                            if (!this.canGrowInto(level.getBlockIdAt((int) vector3.x, (int) vector3.y, (int) vector3.z))) {
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
                Vector3 down = position.down();
                int block = level.getBlockIdAt(down.getFloorX(), down.getFloorY(), down.getFloorZ());

                if ((block == Block.GRASS || block == Block.DIRT) && position.getY() < 256 - i - 1) {
                    this.setDirtAt(level, position.down());
                    BlockFace face = BlockFace.Plane.HORIZONTAL.random(rand);
                    int k2 = i - rand.nextBoundedInt(4) - 1;
                    int l2 = 3 - rand.nextBoundedInt(3);
                    int i3 = position.getFloorX();
                    int j1 = position.getFloorZ();
                    int k1 = 0;

                    for (int l1 = 0; l1 < i; ++l1) {
                        int i2 = position.getFloorY() + l1;

                        if (l1 >= k2 && l2 > 0) {
                            i3 += face.getXOffset();
                            j1 += face.getZOffset();
                            --l2;
                        }

                        Vector3 blockpos = new Vector3(i3, i2, j1);
                        int material = level.getBlockIdAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ());

                        if (material == Block.AIR || material == Block.LEAVES) {
                            this.placeLogAt(level, blockpos);
                            k1 = i2;
                        }
                    }

                    Vector3 blockpos2 = new Vector3(i3, k1, j1);

                    for (int j3 = -3; j3 <= 3; ++j3) {
                        for (int i4 = -3; i4 <= 3; ++i4) {
                            if (Math.abs(j3) != 3 || Math.abs(i4) != 3) {
                                this.placeLeafAt(level, blockpos2.add(j3, 0, i4));
                            }
                        }
                    }

                    blockpos2 = blockpos2.up();

                    for (int k3 = -1; k3 <= 1; ++k3) {
                        for (int j4 = -1; j4 <= 1; ++j4) {
                            this.placeLeafAt(level, blockpos2.add(k3, 0, j4));
                        }
                    }

                    this.placeLeafAt(level, blockpos2.east(2));
                    this.placeLeafAt(level, blockpos2.west(2));
                    this.placeLeafAt(level, blockpos2.south(2));
                    this.placeLeafAt(level, blockpos2.north(2));
                    i3 = position.getFloorX();
                    j1 = position.getFloorZ();
                    BlockFace face1 = BlockFace.Plane.HORIZONTAL.random(rand);

                    if (face1 != face) {
                        int l3 = k2 - rand.nextBoundedInt(2) - 1;
                        int k4 = 1 + rand.nextBoundedInt(3);
                        k1 = 0;

                        for (int l4 = l3; l4 < i && k4 > 0; --k4) {
                            if (l4 >= 1) {
                                int j2 = position.getFloorY() + l4;
                                i3 += face1.getXOffset();
                                j1 += face1.getZOffset();
                                Vector3 blockpos1 = new Vector3(i3, j2, j1);
                                int material1 = level.getBlockIdAt(blockpos1.getFloorX(), blockpos1.getFloorY(), blockpos1.getFloorZ());

                                if (material1 == Block.AIR || material1 == Block.LEAVES) {
                                    this.placeLogAt(level, blockpos1);
                                    k1 = j2;
                                }
                            }

                            ++l4;
                        }

                        if (k1 > 0) {
                            Vector3 blockpos3 = new Vector3(i3, k1, j1);

                            for (int i5 = -2; i5 <= 2; ++i5) {
                                for (int k5 = -2; k5 <= 2; ++k5) {
                                    if (Math.abs(i5) != 2 || Math.abs(k5) != 2) {
                                        this.placeLeafAt(level, blockpos3.add(i5, 0, k5));
                                    }
                                }
                            }

                            blockpos3 = blockpos3.up();

                            for (int j5 = -1; j5 <= 1; ++j5) {
                                for (int l5 = -1; l5 <= 1; ++l5) {
                                    this.placeLeafAt(level, blockpos3.add(j5, 0, l5));
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

    private void placeLogAt(ChunkManager worldIn, Vector3 pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, TRUNK);
    }

    private void placeLeafAt(ChunkManager worldIn, Vector3 pos) {
        int material = worldIn.getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());

        if (material == Block.AIR || material == Block.LEAVES) {
            this.setBlockAndNotifyAdequately(worldIn, pos, LEAF);
        }
    }
}
