package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;

public class PopulatorLava extends Populator {

    private ChunkManager level;

    private int randomAmount;

    private int baseAmount;

    private NukkitRandom random;

    public void setRandomAmount(final int amount) {
        this.randomAmount = amount;
    }

    public void setBaseAmount(final int amount) {
        this.baseAmount = amount;
    }

    @Override
    public void populate(final ChunkManager level, final int chunkX, final int chunkZ, final NukkitRandom random, final FullChunk chunk) {
        this.random = random;
        if (random.nextRange(0, 100) < 5) {
            this.level = level;
            final int amount = random.nextRange(0, this.randomAmount + 1) + this.baseAmount;
            final int bx = chunkX << 4;
            final int bz = chunkZ << 4;
            final int tx = bx + 15;
            final int tz = bz + 15;
            for (int i = 0; i < amount; ++i) {
                final int x = random.nextRange(0, 15);
                final int z = random.nextRange(0, 15);
                final int y = this.getHighestWorkableBlock(chunk, x, z);
                if (y != -1 && chunk.getBlockId(x, y, z) == BlockID.AIR) {
                    chunk.setBlock(x, y, z, BlockID.LAVA);
                    chunk.setBlockLight(x, y, z, Block.light[BlockID.LAVA]);
                    this.lavaSpread(bx + x, y, bz + z);
                }
            }
        }
    }

    private int getFlowDecay(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
        if (this.level.getBlockIdAt(x1, y1, z1) != this.level.getBlockIdAt(x2, y2, z2)) {
            return -1;
        } else {
            return this.level.getBlockDataAt(x2, y2, z2);
        }
    }

    private void lavaSpread(final int x, final int y, final int z) {
        if (this.level.getChunk(x >> 4, z >> 4) == null) {
            return;
        }
        int decay = this.getFlowDecay(x, y, z, x, y, z);
        final int multiplier = 2;
        if (decay > 0) {
            int smallestFlowDecay = -100;
            smallestFlowDecay = this.getSmallestFlowDecay(x, y, z, x, y, z - 1, smallestFlowDecay);
            smallestFlowDecay = this.getSmallestFlowDecay(x, y, z, x, y, z + 1, smallestFlowDecay);
            smallestFlowDecay = this.getSmallestFlowDecay(x, y, z, x - 1, y, z, smallestFlowDecay);
            smallestFlowDecay = this.getSmallestFlowDecay(x, y, z, x + 1, y, z, smallestFlowDecay);
            int k = smallestFlowDecay + multiplier;
            if (k >= 8 || smallestFlowDecay < 0) {
                k = -1;
            }
            final int topFlowDecay = this.getFlowDecay(x, y, z, x, y + 1, z);
            if (topFlowDecay >= 0) {
                if (topFlowDecay >= 8) {
                    k = topFlowDecay;
                } else {
                    k = topFlowDecay | 0x08;
                }
            }
            if (decay < 8 && k < 8 && k > 1 && this.random.nextRange(0, 4) != 0) {
                k = decay;
            }
            if (k != decay) {
                decay = k;
                if (decay < 0) {
                    this.level.setBlockAt(x, y, z, 0);
                } else {
                    this.level.setBlockAt(x, y, z, BlockID.LAVA, decay);
                    this.lavaSpread(x, y, z);
                    return;
                }
            }
        }
        if (this.canFlowInto(x, y - 1, z)) {
            if (decay >= 8) {
                this.flowIntoBlock(x, y - 1, z, decay);
            } else {
                this.flowIntoBlock(x, y - 1, z, decay | 0x08);
            }
        } else if (decay >= 0 && (decay == 0 || !this.canFlowInto(x, y - 1, z))) {
            final boolean[] flags = this.getOptimalFlowDirections(x, y, z);
            int l = decay + multiplier;
            if (decay >= 8) {
                l = 1;
            }
            if (l >= 8) {
                return;
            }
            if (flags[0]) {
                this.flowIntoBlock(x - 1, y, z, l);
            }
            if (flags[1]) {
                this.flowIntoBlock(x + 1, y, z, l);
            }
            if (flags[2]) {
                this.flowIntoBlock(x, y, z - 1, l);
            }
            if (flags[3]) {
                this.flowIntoBlock(x, y, z + 1, l);
            }
        }
    }

    private void flowIntoBlock(final int x, final int y, final int z, final int newFlowDecay) {
        if (this.level.getBlockIdAt(x, y, z) == BlockID.AIR) {
            this.level.setBlockAt(x, y, z, BlockID.LAVA, newFlowDecay);
            this.lavaSpread(x, y, z);
        }
    }

    private boolean canFlowInto(final int x, final int y, final int z) {
        final int id = this.level.getBlockIdAt(x, y, z);
        return id == BlockID.AIR || id == BlockID.LAVA || id == BlockID.STILL_LAVA;
    }

    private int calculateFlowCost(final int xx, final int yy, final int zz, final int accumulatedCost, final int previousDirection) {
        int cost = 1000;
        for (int j = 0; j < 4; ++j) {
            if (
                j == 0 && previousDirection == 1 ||
                    j == 1 && previousDirection == 0 ||
                    j == 2 && previousDirection == 3 ||
                    j == 3 && previousDirection == 2
            ) {
                int x = xx;
                final int y = yy;
                int z = zz;
                if (j == 0) {
                    --x;
                } else if (j == 1) {
                    ++x;
                } else if (j == 2) {
                    --z;
                } else if (j == 3) {
                    ++z;
                }
                if (!this.canFlowInto(x, y, z)) {
                    continue;
                } else if (this.canFlowInto(x, y, z) && this.level.getBlockDataAt(x, y, z) == 0) {
                    continue;
                } else if (this.canFlowInto(x, y - 1, z)) {
                    return accumulatedCost;
                }
                if (accumulatedCost >= 4) {
                    continue;
                }
                final int realCost = this.calculateFlowCost(x, y, z, accumulatedCost + 1, j);
                if (realCost < cost) {
                    cost = realCost;
                }
            }
        }
        return cost;
    }

    private boolean[] getOptimalFlowDirections(final int xx, final int yy, final int zz) {
        final int[] flowCost = {0, 0, 0, 0};
        final boolean[] isOptimalFlowDirection = {false, false, false, false};
        for (int j = 0; j < 4; ++j) {
            flowCost[j] = 1000;
            int x = xx;
            final int y = yy;
            int z = zz;
            if (j == 0) {
                --x;
            } else if (j == 1) {
                ++x;
            } else if (j == 2) {
                --z;
            } else if (j == 3) {
                ++z;
            }
            if (this.canFlowInto(x, y - 1, z)) {
                flowCost[j] = 0;
            } else {
                flowCost[j] = this.calculateFlowCost(x, y, z, 1, j);
            }
        }
        int minCost = flowCost[0];
        for (int i = 1; i < 4; ++i) {
            if (flowCost[i] < minCost) {
                minCost = flowCost[i];
            }
        }
        for (int i = 0; i < 4; ++i) {
            isOptimalFlowDirection[i] = flowCost[i] == minCost;
        }
        return isOptimalFlowDirection;
    }

    private int getSmallestFlowDecay(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2, final int decay) {
        int blockDecay = this.getFlowDecay(x1, y1, z1, x2, y2, z2);
        if (blockDecay < 0) {
            return decay;
        } else if (blockDecay >= 8) {
            blockDecay = 0;
        }
        return decay >= 0 && blockDecay >= decay ? decay : blockDecay;
    }

    private int getHighestWorkableBlock(final FullChunk chunk, final int x, final int z) {
        int y;
        for (y = 127; y >= 0; y--) {
            final int b = chunk.getBlockId(x, y, z);
            if (b == BlockID.AIR) {
                break;
            }
        }
        return y == 0 ? -1 : y;
    }

}
