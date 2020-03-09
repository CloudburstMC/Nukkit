package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockLeaves;
import cn.nukkit.block.BlockLog;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BedrockRandom;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3i;

import static cn.nukkit.block.BlockIds.*;

public class ObjectSwampTree extends TreeGenerator {

    /**
     * The metadata value of the wood to use in tree generation.
     */
    private final Block metaWood = Block.get(BlockIds.LOG, BlockLog.OAK);

    /**
     * The metadata value of the leaves to use in tree generation.
     */
    private final Block metaLeaves = Block.get(BlockIds.LEAVES, BlockLeaves.OAK);

    @Override
    public boolean generate(ChunkManager worldIn, BedrockRandom rand, Vector3i position) {

        int i = rand.nextInt(4) + 5;
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

                for (int l = position.getX() - k; l <= position.getX() + k && flag; ++l) {
                    for (int i1 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
                        if (j >= 0 && j < 256) {
                            if (!this.canGrowInto(worldIn.getBlockId(l, j, i1))) {
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
                Identifier block = worldIn.getBlockId(down);

                if ((block == GRASS || block == DIRT) && position.getY() < 256 - i - 1) {
                    this.setDirtAt(worldIn, down);

                    for (int k1 = position.getY() - 3 + i; k1 <= position.getY() + i; ++k1) {
                        int j2 = k1 - (position.getY() + i);
                        int l2 = 2 - j2 / 2;

                        for (int j3 = position.getX() - l2; j3 <= position.getX() + l2; ++j3) {
                            int k3 = j3 - position.getX();

                            for (int i4 = position.getZ() - l2; i4 <= position.getZ() + l2; ++i4) {
                                int j1 = i4 - position.getZ();

                                if (Math.abs(k3) != l2 || Math.abs(j1) != l2 || rand.nextInt(2) != 0 && j2 != 0) {
                                    Identifier id = worldIn.getBlockId(j3, k1, i4);

                                    if (id == AIR || id == LEAVES || id == VINE) {
                                        worldIn.setBlockAt(j3, k1, i4, this.metaLeaves);
                                    }
                                }
                            }
                        }
                    }

                    for (int l1 = 0; l1 < i; ++l1) {
                        Vector3i up = position.up(l1);
                        Identifier id = worldIn.getBlockId(up);

                        if (id == AIR || id == LEAVES || id == WATER || id == FLOWING_WATER) {
                            worldIn.setBlockAt(up.getX(), up.getY(), up.getZ(), this.metaWood);
                        }
                    }

                    for (int i2 = position.getY() - 3 + i; i2 <= position.getY() + i; ++i2) {
                        int k2 = i2 - (position.getY() + i);
                        int i3 = 2 - k2 / 2;

                        for (int l3 = position.getX() - i3; l3 <= position.getX() + i3; ++l3) {
                            for (int j4 = position.getZ() - i3; j4 <= position.getZ() + i3; ++j4) {
                                Vector3i pos2 = Vector3i.from(l3, i2, j4);

                                if (worldIn.getBlockId(pos2) == LEAVES) {
                                    Vector3i blockpos2 = pos2.west();
                                    Vector3i blockpos3 = pos2.east();
                                    Vector3i blockpos4 = pos2.north();
                                    Vector3i blockpos1 = pos2.south();

                                    if (rand.nextInt(4) == 0 && worldIn.getBlockId(blockpos2) == AIR) {
                                        this.addHangingVine(worldIn, blockpos2, 8);
                                    }

                                    if (rand.nextInt(4) == 0 && worldIn.getBlockId(blockpos3) == AIR) {
                                        this.addHangingVine(worldIn, blockpos3, 2);
                                    }

                                    if (rand.nextInt(4) == 0 && worldIn.getBlockId(blockpos4) == AIR) {
                                        this.addHangingVine(worldIn, blockpos4, 1);
                                    }

                                    if (rand.nextInt(4) == 0 && worldIn.getBlockId(blockpos1) == AIR) {
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

    private void addVine(ChunkManager worldIn, Vector3i pos, int meta) {
        worldIn.setBlockAt(pos.getX(), pos.getY(), pos.getZ(), Block.get(BlockIds.VINE, meta));
    }

    private void addHangingVine(ChunkManager worldIn, Vector3i pos, int meta) {
        this.addVine(worldIn, pos, meta);
        int i = 4;

        for (pos = pos.down(); i > 0 && worldIn.getBlockId(pos) == AIR; --i) {
            this.addVine(worldIn, pos, meta);
            pos = pos.down();
        }
    }
}
