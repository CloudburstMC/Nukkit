package cn.nukkit.server.level.generator.populator;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.level.ChunkManager;
import cn.nukkit.server.level.format.FullChunk;
import cn.nukkit.server.level.format.generic.BaseFullChunk;
import cn.nukkit.server.math.NukkitRandom;

public class PopulatorLava extends Populator {
    private ChunkManager level;
    private int randomAmount;
    private int baseAmount;
    private NukkitRandom random;

    public void setRandomAmount(int amount) {
        this.randomAmount = amount;
    }

    public void setBaseAmount(int amount) {
        this.baseAmount = amount;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random) {
        this.random = random;
        if (random.nextRange(0, 100) < 5) {
            this.level = level;
            int amount = random.nextRange(0, this.randomAmount + 1) + this.baseAmount;
            BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);
            int bx = chunkX << 4;
            int bz = chunkZ << 4;
            int tx = bx + 15;
            int tz = bz + 15;
            for (int i = 0; i < amount; ++i) {
                int x = random.nextRange(0, 15);
                int z = random.nextRange(0, 15);
                int y = this.getHighestWorkableBlock(chunk, x, z);
                if (y != -1 && chunk.getBlockId(x, y, z) == Block.AIR) {
                    chunk.setBlock(x, y, z, Block.LAVA);
                    chunk.setBlockLight(x, y, z, Block.light[Block.LAVA]);
                    this.lavaSpread(bx + x, y, bz + z);
                }
            }
        }
    }

    private int getFlowDecay(int x1, int y1, int z1, int x2, int y2, int z2) {
        if (this.level.getBlockIdAt(x1, y1, z1) != this.level.getBlockIdAt(x2, y2, z2)) {
            return -1;
        } else {
            return this.level.getBlockDataAt(x2, y2, z2);
        }
    }

    private void lavaSpread(int x, int y, int z) {
        if (this.level.getChunk(x >> 4, z >> 4) == null) {
            return;
        }
        int decay = this.getFlowDecay(x, y, z, x, y, z);
        int multiplier = 2;
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
            int topFlowDecay = this.getFlowDecay(x, y, z, x, y + 1, z);
            if (topFlowDecay >= 0) {
                if (topFlowDecay >= 8) {
                    k = topFlowDecay;
                } else {
                    k = topFlowDecay | 0x08;
                }
            }
            if (decay < 8 && k < 8 && k > 1 && random.nextRange(0, 4) != 0) {
                k = decay;
            }
            if (k != decay) {
                decay = k;
                if (decay < 0) {
                    this.level.setBlockIdAt(x, y, z, 0);
                } else {
                    this.level.setBlockIdAt(x, y, z, Block.LAVA);
                    this.level.setBlockDataAt(x, y, z, decay);
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
            boolean[] flags = this.getOptimalFlowDirections(x, y, z);
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

    private void flowIntoBlock(int x, int y, int z, int newFlowDecay) {
        if (this.level.getBlockIdAt(x, y, z) == Block.AIR) {
            this.level.setBlockIdAt(x, y, z, Block.LAVA);
            this.level.setBlockDataAt(x, y, z, newFlowDecay);
            this.lavaSpread(x, y, z);
        }
    }

    private boolean canFlowInto(int x, int y, int z) {
        int id = this.level.getBlockIdAt(x, y, z);
        return id == Block.AIR || id == Block.LAVA || id == Block.STILL_LAVA;
    }

    private int calculateFlowCost(int xx, int yy, int zz, int accumulatedCost, int previousDirection) {
        int cost = 1000;
        for (int j = 0; j < 4; ++j) {
            if (
                    (j == 0 && previousDirection == 1) ||
                            (j == 1 && previousDirection == 0) ||
                            (j == 2 && previousDirection == 3) ||
                            (j == 3 && previousDirection == 2)
                    ) {
                int x = xx;
                int y = yy;
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
                int realCost = this.calculateFlowCost(x, y, z, accumulatedCost + 1, j);
                if (realCost < cost) {
                    cost = realCost;
                }
            }
        }
        return cost;
    }

    private boolean[] getOptimalFlowDirections(int xx, int yy, int zz) {
        int[] flowCost = {0, 0, 0, 0};
        boolean[] isOptimalFlowDirection = {false, false, false, false};
        for (int j = 0; j < 4; ++j) {
            flowCost[j] = 1000;
            int x = xx;
            int y = yy;
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
            isOptimalFlowDirection[i] = (flowCost[i] == minCost);
        }
        return isOptimalFlowDirection;
    }

    private int getSmallestFlowDecay(int x1, int y1, int z1, int x2, int y2, int z2, int decay) {
        int blockDecay = this.getFlowDecay(x1, y1, z1, x2, y2, z2);
        if (blockDecay < 0) {
            return decay;
        } else if (blockDecay >= 8) {
            blockDecay = 0;
        }
        return (decay >= 0 && blockDecay >= decay) ? decay : blockDecay;
    }


    private int getHighestWorkableBlock(FullChunk chunk, int x, int z) {
        int y;
        for (y = 127; y >= 0; y--) {
            int b = chunk.getBlockId(x, y, z);
            if (b == Block.AIR) {
                break;
            }
        }
        return y == 0 ? -1 : y;
    }
}
