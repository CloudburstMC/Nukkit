package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.BedrockRandom;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.*;

public class PopulatorLava extends Populator {
    private int randomAmount;
    private int baseAmount;

    public void setRandomAmount(int amount) {
        this.randomAmount = amount;
    }

    public void setBaseAmount(int amount) {
        this.baseAmount = amount;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, BedrockRandom random, IChunk chunk) {
        if (random.nextInt(0, 100) < 5) {
            int amount = random.nextInt(this.randomAmount + 1) + this.baseAmount;
            int bx = chunkX << 4;
            int bz = chunkZ << 4;
            int tx = bx + 15;
            int tz = bz + 15;
            for (int i = 0; i < amount; ++i) {
                int x = random.nextInt(0, 15);
                int z = random.nextInt(0, 15);
                int y = this.getHighestWorkableBlock(chunk, x, z);
                if (y != -1 && chunk.getBlockId(x, y, z) == AIR) {
                    chunk.setBlockId(x, y, z, 0, FLOWING_LAVA);
                    chunk.setBlockLight(x, y, z, BlockRegistry.get().getBlock(FLOWING_LAVA, 0).getLightLevel());
                    //this.lavaSpread(level, random, bx + x, y, bz + z); <--- Causes errors
                }
            }
        }
    }

    private int getFlowDecay(ChunkManager level, int x1, int y1, int z1, int x2, int y2, int z2) {
        if (level.getBlockIdAt(x1, y1, z1) != level.getBlockIdAt(x2, y2, z2)) {
            return -1;
        } else {
            return level.getBlockDataAt(x2, y2, z2);
        }
    }

    private void lavaSpread(ChunkManager level, BedrockRandom random, int x, int y, int z) {
        if (level.getChunk(x >> 4, z >> 4) == null) {
            return;
        }
        int decay = this.getFlowDecay(level, x, y, z, x, y, z);
        int multiplier = 2;
        if (decay > 0) {
            int smallestFlowDecay = -100;
            smallestFlowDecay = this.getSmallestFlowDecay(level, x, y, z, x, y, z - 1, smallestFlowDecay);
            smallestFlowDecay = this.getSmallestFlowDecay(level, x, y, z, x, y, z + 1, smallestFlowDecay);
            smallestFlowDecay = this.getSmallestFlowDecay(level, x, y, z, x - 1, y, z, smallestFlowDecay);
            smallestFlowDecay = this.getSmallestFlowDecay(level, x, y, z, x + 1, y, z, smallestFlowDecay);
            int k = smallestFlowDecay + multiplier;
            if (k >= 8 || smallestFlowDecay < 0) {
                k = -1;
            }
            int topFlowDecay = this.getFlowDecay(level, x, y, z, x, y + 1, z);
            if (topFlowDecay >= 0) {
                if (topFlowDecay >= 8) {
                    k = topFlowDecay;
                } else {
                    k = topFlowDecay | 0x08;
                }
            }
            if (decay < 8 && k < 8 && k > 1 && random.nextInt(0, 4) != 0) {
                k = decay;
            }
            if (k != decay) {
                decay = k;
                if (decay < 0) {
                    level.setBlockIdAt(x, y, z, AIR);
                } else {
                    level.setBlockAt(x, y, z, FLOWING_LAVA, decay);
                    this.lavaSpread(level, random, x, y, z);
                    return;
                }
            }
        }
        if (this.canFlowInto(level, x, y - 1, z)) {
            if (decay >= 8) {
                this.flowIntoBlock(level, random, x, y - 1, z, decay);
            } else {
                this.flowIntoBlock(level, random, x, y - 1, z, decay | 0x08);
            }
        } else if (decay >= 0 && (decay == 0 || !this.canFlowInto(level, x, y - 1, z))) {
            boolean[] flags = this.getOptimalFlowDirections(level, x, y, z);
            int l = decay + multiplier;
            if (decay >= 8) {
                l = 1;
            }
            if (l >= 8) {
                return;
            }
            if (flags[0]) {
                this.flowIntoBlock(level, random, x - 1, y, z, l);
            }
            if (flags[1]) {
                this.flowIntoBlock(level, random, x + 1, y, z, l);
            }
            if (flags[2]) {
                this.flowIntoBlock(level, random, x, y, z - 1, l);
            }
            if (flags[3]) {
                this.flowIntoBlock(level, random, x, y, z + 1, l);
            }
        }
    }

    private void flowIntoBlock(ChunkManager level, BedrockRandom random, int x, int y, int z, int newFlowDecay) {
        if (level.getBlockIdAt(x, y, z) == AIR) {
            level.setBlockAt(x, y, z, FLOWING_LAVA, newFlowDecay);
            this.lavaSpread(level, random, x, y, z);
        }
    }

    private boolean canFlowInto(ChunkManager level, int x, int y, int z) {
        Identifier id = level.getBlockIdAt(x, y, z);
        return id == AIR || id == FLOWING_LAVA || id == LAVA;
    }

    private int calculateFlowCost(ChunkManager level, int xx, int yy, int zz, int accumulatedCost, int previousDirection) {
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
                if (!this.canFlowInto(level, x, y, z)) {
                    continue;
                } else if (this.canFlowInto(level, x, y, z) && level.getBlockDataAt(x, y, z) == 0) {
                    continue;
                } else if (this.canFlowInto(level, x, y - 1, z)) {
                    return accumulatedCost;
                }
                if (accumulatedCost >= 4) {
                    continue;
                }
                int realCost = this.calculateFlowCost(level, x, y, z, accumulatedCost + 1, j);
                if (realCost < cost) {
                    cost = realCost;
                }
            }
        }
        return cost;
    }

    private boolean[] getOptimalFlowDirections(ChunkManager level, int xx, int yy, int zz) {
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
            if (this.canFlowInto(level, x, y - 1, z)) {
                flowCost[j] = 0;
            } else {
                flowCost[j] = this.calculateFlowCost(level, x, y, z, 1, j);
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

    private int getSmallestFlowDecay(ChunkManager level, int x1, int y1, int z1, int x2, int y2, int z2, int decay) {
        int blockDecay = this.getFlowDecay(level, x1, y1, z1, x2, y2, z2);
        if (blockDecay < 0) {
            return decay;
        } else if (blockDecay >= 8) {
            blockDecay = 0;
        }
        return (decay >= 0 && blockDecay >= decay) ? decay : blockDecay;
    }


    private int getHighestWorkableBlock(IChunk chunk, int x, int z) {
        int y;
        for (y = 127; y >= 0; y--) {
            Identifier b = chunk.getBlockId(x, y, z);
            if (b == AIR) {
                break;
            }
        }
        return y == 0 ? -1 : y;
    }
}
