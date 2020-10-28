package cn.nukkit.level.generator.object.mushroom;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.object.BasicGenerator;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;

import java.util.List;

public class BigMushroom extends BasicGenerator {
    public static final int NORTH_WEST = 1;
    public static final int NORTH = 2;
    public static final int NORTH_EAST = 3;
    public static final int WEST = 4;
    public static final int CENTER = 5;
    public static final int EAST = 6;
    public static final int SOUTH_WEST = 7;
    public static final int SOUTH = 8;
    public static final int SOUTH_EAST = 9;
    public static final int STEM = 10;
    public static final int ALL_INSIDE = 0;
    public static final int ALL_OUTSIDE = 14;
    public static final int ALL_STEM = 15;

    public static final int BROWN = 0;
    public static final int RED = 1;
    /**
     * The mushroom type. 0 for brown, 1 for red.
     */
    private int mushroomType;

    public BigMushroom(int mushroomType) {
        this.mushroomType = mushroomType;
    }

    public BigMushroom() {
        this.mushroomType = -1;
    }

    public boolean generate(ChunkManager level, List<Block> blocks, NukkitRandom rand, BlockVector3 position) {
        int block = this.mushroomType;
        if (block < 0) {
            block = rand.nextBoolean() ? RED : BROWN;
        }

        Block mushroom = block == 0 ? Block.get(BlockID.BROWN_MUSHROOM_BLOCK) : Block.get(BlockID.RED_MUSHROOM_BLOCK);

        int i = rand.nextBoundedInt(3) + 4;

        if (rand.nextBoundedInt(12) == 0) {
            i *= 2;
        }

        boolean flag = true;

        if (position.getY() >= 1 && position.getY() + i + 1 < 256) {
            for (int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
                int k = 3;

                if (j <= position.getY() + 3) {
                    k = 0;
                }

                BlockVector3 pos = new BlockVector3();

                for (int l = position.getX() - k; l <= position.getX() + k && flag; ++l) {
                    for (int i1 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
                        if (j >= 0 && j < 256) {
                            pos.setComponents(l, j, i1);
                            int material = level.getBlockIdAt(pos.getX(), pos.getY(), pos.getZ());

                            if (material != Block.AIR && material != Block.LEAVES) {
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
                BlockVector3 pos2 = position.down();
                int block1 = level.getBlockIdAt(pos2.getX(), pos2.getY(), pos2.getZ());

                if (block1 != Block.DIRT && block1 != Block.GRASS && block1 != Block.MYCELIUM) {
                    return false;
                } else {
                    int k2 = position.getY() + i;

                    if (block == RED) {
                        k2 = position.getY() + i - 3;
                    }

                    for (int l2 = k2; l2 <= position.getY() + i; ++l2) {
                        int j3 = 1;

                        if (l2 < position.getY() + i) {
                            ++j3;
                        }

                        if (block == BROWN) {
                            j3 = 3;
                        }

                        int k3 = position.getX() - j3;
                        int l3 = position.getX() + j3;
                        int j1 = position.getZ() - j3;
                        int k1 = position.getZ() + j3;

                        for (int l1 = k3; l1 <= l3; ++l1) {
                            for (int i2 = j1; i2 <= k1; ++i2) {
                                int j2 = 5;

                                if (l1 == k3) {
                                    --j2;
                                } else if (l1 == l3) {
                                    ++j2;
                                }

                                if (i2 == j1) {
                                    j2 -= 3;
                                } else if (i2 == k1) {
                                    j2 += 3;
                                }

                                int meta = j2;

                                if (block == BROWN || l2 < position.getY() + i) {
                                    if ((l1 == k3 || l1 == l3) && (i2 == j1 || i2 == k1)) {
                                        continue;
                                    }

                                    if (l1 == position.getX() - (j3 - 1) && i2 == j1) {
                                        meta = NORTH_WEST;
                                    }

                                    if (l1 == k3 && i2 == position.getZ() - (j3 - 1)) {
                                        meta = NORTH_WEST;
                                    }

                                    if (l1 == position.getX() + (j3 - 1) && i2 == j1) {
                                        meta = NORTH_EAST;
                                    }

                                    if (l1 == l3 && i2 == position.getZ() - (j3 - 1)) {
                                        meta = NORTH_EAST;
                                    }

                                    if (l1 == position.getX() - (j3 - 1) && i2 == k1) {
                                        meta = SOUTH_WEST;
                                    }

                                    if (l1 == k3 && i2 == position.getZ() + (j3 - 1)) {
                                        meta = SOUTH_WEST;
                                    }

                                    if (l1 == position.getX() + (j3 - 1) && i2 == k1) {
                                        meta = SOUTH_EAST;
                                    }

                                    if (l1 == l3 && i2 == position.getZ() + (j3 - 1)) {
                                        meta = SOUTH_EAST;
                                    }
                                }

                                if (meta == CENTER && l2 < position.getY() + i) {
                                    meta = ALL_INSIDE;
                                }

                                if (position.getY() >= position.getY() + i - 1 || meta != ALL_INSIDE) {
                                    BlockVector3 blockPos = new BlockVector3(l1, l2, i2);

                                    if (!Block.solid[level.getBlockIdAt(blockPos.getX(), blockPos.getY(), blockPos.getZ())]) {
                                        mushroom.setDamage(meta);
                                        this.setBlockAndNotifyAdequately(blocks, blockPos, mushroom);
                                    }
                                }
                            }
                        }
                    }

                    for (int i3 = 0; i3 < i; ++i3) {
                        BlockVector3 pos = position.up(i3);
                        int id = level.getBlockIdAt(pos.getX(), pos.getY(), pos.getZ());

                        if (!Block.solid[id]) {
                            mushroom.setDamage(STEM);
                            this.setBlockAndNotifyAdequately(blocks, pos, mushroom);
                        }
                    }

                    return true;
                }
            }
        } else {
            return false;
        }
    }
}
