package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockLeaves2;
import cn.nukkit.block.BlockLog2;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BedrockRandom;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3i;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.*;

public class ObjectSavannaTree extends TreeGenerator {
    private static final Block TRUNK = Block.get(LOG2, BlockLog2.ACACIA);
    private static final Block LEAF = Block.get(BlockIds.LEAVES2, BlockLeaves2.ACACIA);

    public boolean generate(ChunkManager level, BedrockRandom rand, Vector3i position) {
        int i = rand.nextInt(3) + rand.nextInt(3) + 5;
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

                Vector3i vector3 = new Vector3i();

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
                Vector3i down = position.down();
                Identifier block = level.getBlockIdAt(down.getX(), down.getY(), down.getZ());

                if ((block == GRASS || block == DIRT) && position.getY() < 256 - i - 1) {
                    this.setDirtAt(level, position.down());
                    BlockFace face = BlockFace.Plane.HORIZONTAL.random(rand);
                    int k2 = i - rand.nextInt(4) - 1;
                    int l2 = 3 - rand.nextInt(3);
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

                        Vector3i blockpos = new Vector3i(i3, i2, j1);
                        Identifier material = level.getBlockIdAt(blockpos.getX(), blockpos.getY(), blockpos.getZ());

                        if (material == AIR || material == LEAVES) {
                            this.placeLogAt(level, blockpos);
                            k1 = i2;
                        }
                    }

                    Vector3i blockpos2 = new Vector3i(i3, k1, j1);

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
                    i3 = position.getX();
                    j1 = position.getZ();
                    BlockFace face1 = BlockFace.Plane.HORIZONTAL.random(rand);

                    if (face1 != face) {
                        int l3 = k2 - rand.nextInt(2) - 1;
                        int k4 = 1 + rand.nextInt(3);
                        k1 = 0;

                        for (int l4 = l3; l4 < i && k4 > 0; --k4) {
                            if (l4 >= 1) {
                                int j2 = position.getY() + l4;
                                i3 += face1.getXOffset();
                                j1 += face1.getZOffset();
                                Vector3i blockpos1 = new Vector3i(i3, j2, j1);
                                Identifier material1 = level.getBlockIdAt(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());

                                if (material1 == AIR || material1 == LEAVES) {
                                    this.placeLogAt(level, blockpos1);
                                    k1 = j2;
                                }
                            }

                            ++l4;
                        }

                        if (k1 > 0) {
                            Vector3i blockpos3 = new Vector3i(i3, k1, j1);

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

    private void placeLogAt(ChunkManager worldIn, Vector3i pos) {
        worldIn.setBlockAt(pos.getX(), pos.getY(), pos.getZ(), TRUNK);
    }

    private void placeLeafAt(ChunkManager worldIn, Vector3i pos) {
        Identifier material = worldIn.getBlockIdAt(pos.getX(), pos.getY(), pos.getZ());

        if (material == AIR || material == LEAVES) {
            worldIn.setBlockAt(pos.getX(), pos.getY(), pos.getZ(), LEAF);
        }
    }
}
