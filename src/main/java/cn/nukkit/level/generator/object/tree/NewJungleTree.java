package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockLeaves;
import cn.nukkit.block.BlockLog;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BedrockRandom;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3i;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.*;

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
    private final Block metaWood = Block.get(BlockIds.LOG, BlockLog.JUNGLE);

    /**
     * The metadata value of the leaves to use in tree generation.
     */
    private final Block metaLeaves = Block.get(BlockIds.LEAVES, BlockLeaves.JUNGLE);

    public NewJungleTree(int minTreeHeight, int maxTreeHeight) {
        this.minTreeHeight = minTreeHeight;
        this.maxTreeHeight = maxTreeHeight;
    }

    private static boolean isAirBlock(ChunkManager level, Vector3i v) {
        return level.getBlockIdAt(v.x, v.y, v.z) == AIR;
    }

    @Override
    public boolean generate(ChunkManager worldIn, BedrockRandom random, Vector3i vectorPosition) {
        Vector3i position = new Vector3i(vectorPosition.getX(), vectorPosition.getY(), vectorPosition.getZ());

        int i = random.nextInt(maxTreeHeight) + this.minTreeHeight;
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

                Vector3i pos2 = new Vector3i();

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
                Vector3i down = position.down();
                Identifier block = worldIn.getBlockIdAt(down.x, down.y, down.z);

                if ((block == GRASS || block == DIRT || block == FARMLAND) && position.getY() < 256 - i - 1) {
                    this.setDirtAt(worldIn, down);
                    int k2 = 3;
                    int l2 = 0;

                    for (int i3 = position.getY() - 3 + i; i3 <= position.getY() + i; ++i3) {
                        int i4 = i3 - (position.getY() + i);
                        int j1 = 1 - i4 / 2;

                        for (int k1 = position.getX() - j1; k1 <= position.getX() + j1; ++k1) {
                            int l1 = k1 - position.getX();

                            for (int i2 = position.getZ() - j1; i2 <= position.getZ() + j1; ++i2) {
                                int j2 = i2 - position.getZ();

                                if (Math.abs(l1) != j1 || Math.abs(j2) != j1 || random.nextInt(2) != 0 && i4 != 0) {
                                    Identifier id = worldIn.getBlockIdAt(k1, i3, i2);

                                    if (id == AIR || id == LEAVES || id == VINE) {
                                        worldIn.setBlockAt(k1, i3, i2, this.metaLeaves);
                                    }
                                }
                            }
                        }
                    }

                    for (int j3 = 0; j3 < i; ++j3) {
                        Vector3i up = position.up(j3);
                        Identifier id = worldIn.getBlockIdAt(up.x, up.y, up.z);

                        if (id == AIR || id == LEAVES || id == VINE) {
                            worldIn.setBlockAt(up.getX(), up.getY(), up.getZ(), this.metaWood);

                            if (j3 > 0) {
                                if (random.nextInt(3) > 0 && isAirBlock(worldIn, position.add(-1, j3, 0))) {
                                    this.addVine(worldIn, position.add(-1, j3, 0), 8);
                                }

                                if (random.nextInt(3) > 0 && isAirBlock(worldIn, position.add(1, j3, 0))) {
                                    this.addVine(worldIn, position.add(1, j3, 0), 2);
                                }

                                if (random.nextInt(3) > 0 && isAirBlock(worldIn, position.add(0, j3, -1))) {
                                    this.addVine(worldIn, position.add(0, j3, -1), 1);
                                }

                                if (random.nextInt(3) > 0 && isAirBlock(worldIn, position.add(0, j3, 1))) {
                                    this.addVine(worldIn, position.add(0, j3, 1), 4);
                                }
                            }
                        }
                    }

                    for (int k3 = position.getY() - 3 + i; k3 <= position.getY() + i; ++k3) {
                        int j4 = k3 - (position.getY() + i);
                        int k4 = 2 - j4 / 2;
                        Vector3i pos2 = new Vector3i();

                        for (int l4 = position.getX() - k4; l4 <= position.getX() + k4; ++l4) {
                            for (int i5 = position.getZ() - k4; i5 <= position.getZ() + k4; ++i5) {
                                pos2.setComponents(l4, k3, i5);

                                if (worldIn.getBlockIdAt(pos2.x, pos2.y, pos2.z) == LEAVES) {
                                    Vector3i blockpos2 = pos2.west();
                                    Vector3i blockpos3 = pos2.east();
                                    Vector3i blockpos4 = pos2.north();
                                    Vector3i blockpos1 = pos2.south();

                                    if (random.nextInt(4) == 0 && worldIn.getBlockIdAt(blockpos2.x, blockpos2.y, blockpos2.z) == AIR) {
                                        this.addHangingVine(worldIn, blockpos2, 8);
                                    }

                                    if (random.nextInt(4) == 0 && worldIn.getBlockIdAt(blockpos3.x, blockpos3.y, blockpos3.z) == AIR) {
                                        this.addHangingVine(worldIn, blockpos3, 2);
                                    }

                                    if (random.nextInt(4) == 0 && worldIn.getBlockIdAt(blockpos4.x, blockpos4.y, blockpos4.z) == AIR) {
                                        this.addHangingVine(worldIn, blockpos4, 1);
                                    }

                                    if (random.nextInt(4) == 0 && worldIn.getBlockIdAt(blockpos1.x, blockpos1.y, blockpos1.z) == AIR) {
                                        this.addHangingVine(worldIn, blockpos1, 4);
                                    }
                                }
                            }
                        }
                    }

                    if (random.nextInt(5) == 0 && i > 5) {
                        for (int l3 = 0; l3 < 2; ++l3) {
                            for (BlockFace enumfacing : BlockFace.Plane.HORIZONTAL) {
                                if (random.nextInt(4 - l3) == 0) {
                                    BlockFace enumfacing1 = enumfacing.getOpposite();
                                    this.placeCocoa(worldIn, random.nextInt(3), position.add(enumfacing1.getXOffset(), i - 5 + l3, enumfacing1.getZOffset()), enumfacing);
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

    private void placeCocoa(ChunkManager worldIn, int age, Vector3i pos, BlockFace side) {
        int meta = getCocoaMeta(age, side.getIndex());

        worldIn.setBlockAt(pos.getX(), pos.getY(), pos.getZ(), Block.get(COCOA, meta));
    }

    private void addVine(ChunkManager worldIn, Vector3i pos, int meta) {
        worldIn.setBlockAt(pos.getX(), pos.getY(), pos.getZ(), Block.get(BlockIds.VINE, meta));
    }

    private void addHangingVine(ChunkManager worldIn, Vector3i pos, int meta) {
        this.addVine(worldIn, pos, meta);
        int i = 4;

        for (pos = pos.down(); i > 0 && worldIn.getBlockIdAt(pos.x, pos.y, pos.z) == AIR; --i) {
            this.addVine(worldIn, pos, meta);
            pos = pos.down();
        }
    }

    private int getCocoaMeta(int age, int side) {
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
