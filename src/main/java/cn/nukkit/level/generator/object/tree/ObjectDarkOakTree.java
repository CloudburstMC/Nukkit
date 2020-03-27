package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockLeaves2;
import cn.nukkit.block.BlockLog2;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BedrockRandom;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3i;

import static cn.nukkit.block.BlockIds.*;

/**
 * Created by CreeperFace on 23. 10. 2016.
 */
public class ObjectDarkOakTree extends TreeGenerator {
    private static final Block DARK_OAK_LOG = Block.get(BlockIds.LOG2, BlockLog2.DARK_OAK);
    private static final Block DARK_OAK_LEAVES = Block.get(BlockIds.LEAVES2, BlockLeaves2.DARK_OAK);

    @Override
    public boolean generate(ChunkManager level, BedrockRandom rand, Vector3i position) {
        int i = rand.nextInt(3) + rand.nextInt(2) + 6;
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();

        if (y >= 1 && y + i + 1 < 256) {
            Vector3i blockpos = position.down();
            Identifier block = level.getBlockId(blockpos);

            if (block != GRASS && block != DIRT) {
                return false;
            } else if (!this.placeTreeOfHeight(level, position, i)) {
                return false;
            } else {
                this.setDirtAt(level, blockpos);
                this.setDirtAt(level, blockpos.east());
                this.setDirtAt(level, blockpos.south());
                this.setDirtAt(level, blockpos.south().east());
                BlockFace enumfacing = BlockFace.Plane.HORIZONTAL.random(rand);
                int i1 = i - rand.nextInt(4);
                int j1 = 2 - rand.nextInt(3);
                int k1 = x;
                int l1 = z;
                int i2 = y + i - 1;

                for (int j2 = 0; j2 < i; ++j2) {
                    if (j2 >= i1 && j1 > 0) {
                        k1 += enumfacing.getXOffset();
                        l1 += enumfacing.getZOffset();
                        --j1;
                    }

                    int k2 = y + j2;
                    Identifier material = level.getBlockId(k1, k2, l1);

                    if (material == AIR || material == LEAVES) {
                        this.placeLogAt(level, k1, k2, l1);
                        this.placeLogAt(level, k1 + 1, k2, l1);
                        this.placeLogAt(level, k1, k2, l1 + 1);
                        this.placeLogAt(level, k1 + 1, k2, l1 + 1);
                    }
                }

                for (int i3 = -2; i3 <= 0; ++i3) {
                    for (int l3 = -2; l3 <= 0; ++l3) {
                        int k4 = -1;
                        this.placeLeafAt(level, k1 + i3, i2 + k4, l1 + l3);
                        this.placeLeafAt(level, 1 + k1 - i3, i2 + k4, l1 + l3);
                        this.placeLeafAt(level, k1 + i3, i2 + k4, 1 + l1 - l3);
                        this.placeLeafAt(level, 1 + k1 - i3, i2 + k4, 1 + l1 - l3);

                        if ((i3 > -2 || l3 > -1) && (i3 != -1 || l3 != -2)) {
                            k4 = 1;
                            this.placeLeafAt(level, k1 + i3, i2 + k4, l1 + l3);
                            this.placeLeafAt(level, 1 + k1 - i3, i2 + k4, l1 + l3);
                            this.placeLeafAt(level, k1 + i3, i2 + k4, 1 + l1 - l3);
                            this.placeLeafAt(level, 1 + k1 - i3, i2 + k4, 1 + l1 - l3);
                        }
                    }
                }

                if (rand.nextBoolean()) {
                    this.placeLeafAt(level, k1, i2 + 2, l1);
                    this.placeLeafAt(level, k1 + 1, i2 + 2, l1);
                    this.placeLeafAt(level, k1 + 1, i2 + 2, l1 + 1);
                    this.placeLeafAt(level, k1, i2 + 2, l1 + 1);
                }

                for (int j3 = -3; j3 <= 4; ++j3) {
                    for (int i4 = -3; i4 <= 4; ++i4) {
                        if ((j3 != -3 || i4 != -3) && (j3 != -3 || i4 != 4) && (j3 != 4 || i4 != -3) && (j3 != 4 || i4 != 4) && (Math.abs(j3) < 3 || Math.abs(i4) < 3)) {
                            this.placeLeafAt(level, k1 + j3, i2, l1 + i4);
                        }
                    }
                }

                for (int k3 = -1; k3 <= 2; ++k3) {
                    for (int j4 = -1; j4 <= 2; ++j4) {
                        if ((k3 < 0 || k3 > 1 || j4 < 0 || j4 > 1) && rand.nextInt(3) <= 0) {
                            int l4 = rand.nextInt(3) + 2;

                            for (int i5 = 0; i5 < l4; ++i5) {
                                this.placeLogAt(level, x + k3, i2 - i5 - 1, z + j4);
                            }

                            for (int j5 = -1; j5 <= 1; ++j5) {
                                for (int l2 = -1; l2 <= 1; ++l2) {
                                    this.placeLeafAt(level, k1 + k3 + j5, i2, l1 + j4 + l2);
                                }
                            }

                            for (int k5 = -2; k5 <= 2; ++k5) {
                                for (int l5 = -2; l5 <= 2; ++l5) {
                                    if (Math.abs(k5) != 2 || Math.abs(l5) != 2) {
                                        this.placeLeafAt(level, k1 + k3 + k5, i2 - 1, l1 + j4 + l5);
                                    }
                                }
                            }
                        }
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    private boolean placeTreeOfHeight(ChunkManager worldIn, Vector3i pos, int height) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        for (int l = 0; l <= height + 1; ++l) {
            int i1 = 1;

            if (l == 0) {
                i1 = 0;
            }

            if (l >= height - 1) {
                i1 = 2;
            }

            for (int j1 = -i1; j1 <= i1; ++j1) {
                for (int k1 = -i1; k1 <= i1; ++k1) {
                    if (!this.canGrowInto(worldIn.getBlockId(x + j1, y + l, z + k1))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private void placeLogAt(ChunkManager worldIn, int x, int y, int z) {
        if (this.canGrowInto(worldIn.getBlockId(x, y, z))) {
            worldIn.setBlockAt(x, y, z, DARK_OAK_LOG);
        }
    }

    private void placeLeafAt(ChunkManager worldIn, int x, int y, int z) {
        Identifier material = worldIn.getBlockId(x, y, z);

        if (material == AIR) {
            worldIn.setBlockAt(x, y, z, DARK_OAK_LEAVES);
        }
    }
}
