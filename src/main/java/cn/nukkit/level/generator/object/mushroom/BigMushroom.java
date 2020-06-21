package cn.nukkit.level.generator.object.mushroom;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.object.BasicGenerator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

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
    private final int mushroomType;

    public BigMushroom(final int mushroomType) {
        this.mushroomType = mushroomType;
    }

    public BigMushroom() {
        this.mushroomType = -1;
    }

    @Override
    public boolean generate(final ChunkManager level, final NukkitRandom rand, final Vector3 position) {
        int block = this.mushroomType;
        if (block < 0) {
            block = rand.nextBoolean() ? BigMushroom.RED : BigMushroom.BROWN;
        }

        final Block mushroom = block == 0 ? Block.get(BlockID.BROWN_MUSHROOM_BLOCK) : Block.get(BlockID.RED_MUSHROOM_BLOCK);

        int i = rand.nextBoundedInt(3) + 4;

        if (rand.nextBoundedInt(12) == 0) {
            i *= 2;
        }

        boolean flag = true;

        if (position.getY() >= 1 && position.getY() + i + 1 < 256) {
            for (int j = position.getFloorY(); j <= position.getY() + 1 + i; ++j) {
                int k = 3;

                if (j <= position.getY() + 3) {
                    k = 0;
                }

                final Vector3 pos = new Vector3();

                for (int l = position.getFloorX() - k; l <= position.getX() + k && flag; ++l) {
                    for (int i1 = position.getFloorZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
                        if (j >= 0 && j < 256) {
                            pos.setComponents(l, j, i1);
                            final int material = level.getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());

                            if (material != BlockID.AIR && material != BlockID.LEAVES) {
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
                final Vector3 pos2 = position.down();
                final int block1 = level.getBlockIdAt(pos2.getFloorX(), pos2.getFloorY(), pos2.getFloorZ());

                if (block1 != BlockID.DIRT && block1 != BlockID.GRASS && block1 != BlockID.MYCELIUM) {
                    return false;
                } else {
                    int k2 = position.getFloorY() + i;

                    if (block == BigMushroom.RED) {
                        k2 = position.getFloorY() + i - 3;
                    }

                    for (int l2 = k2; l2 <= position.getY() + i; ++l2) {
                        int j3 = 1;

                        if (l2 < position.getY() + i) {
                            ++j3;
                        }

                        if (block == BigMushroom.BROWN) {
                            j3 = 3;
                        }

                        final int k3 = position.getFloorX() - j3;
                        final int l3 = position.getFloorX() + j3;
                        final int j1 = position.getFloorZ() - j3;
                        final int k1 = position.getFloorZ() + j3;

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

                                if (block == BigMushroom.BROWN || l2 < position.getY() + i) {
                                    if ((l1 == k3 || l1 == l3) && (i2 == j1 || i2 == k1)) {
                                        continue;
                                    }

                                    if (l1 == position.getX() - (j3 - 1) && i2 == j1) {
                                        meta = BigMushroom.NORTH_WEST;
                                    }

                                    if (l1 == k3 && i2 == position.getZ() - (j3 - 1)) {
                                        meta = BigMushroom.NORTH_WEST;
                                    }

                                    if (l1 == position.getX() + (j3 - 1) && i2 == j1) {
                                        meta = BigMushroom.NORTH_EAST;
                                    }

                                    if (l1 == l3 && i2 == position.getZ() - (j3 - 1)) {
                                        meta = BigMushroom.NORTH_EAST;
                                    }

                                    if (l1 == position.getX() - (j3 - 1) && i2 == k1) {
                                        meta = BigMushroom.SOUTH_WEST;
                                    }

                                    if (l1 == k3 && i2 == position.getZ() + (j3 - 1)) {
                                        meta = BigMushroom.SOUTH_WEST;
                                    }

                                    if (l1 == position.getX() + (j3 - 1) && i2 == k1) {
                                        meta = BigMushroom.SOUTH_EAST;
                                    }

                                    if (l1 == l3 && i2 == position.getZ() + (j3 - 1)) {
                                        meta = BigMushroom.SOUTH_EAST;
                                    }
                                }

                                if (meta == BigMushroom.CENTER && l2 < position.getY() + i) {
                                    meta = BigMushroom.ALL_INSIDE;
                                }

                                if (position.getY() >= position.getY() + i - 1 || meta != BigMushroom.ALL_INSIDE) {
                                    final Vector3 blockPos = new Vector3(l1, l2, i2);

                                    if (!Block.solid[level.getBlockIdAt(blockPos.getFloorX(), blockPos.getFloorY(), blockPos.getFloorZ())]) {
                                        mushroom.setDamage(meta);
                                        this.setBlockAndNotifyAdequately(level, blockPos, mushroom);
                                    }
                                }
                            }
                        }
                    }

                    for (int i3 = 0; i3 < i; ++i3) {
                        final Vector3 pos = position.up(i3);
                        final int id = level.getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());

                        if (!Block.solid[id]) {
                            mushroom.setDamage(BigMushroom.STEM);
                            this.setBlockAndNotifyAdequately(level, pos, mushroom);
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
