package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.Utils;

/**
 * @author DaPorkchop_
 * Nukkit Project
 */
public class ObjectBigSpruceTree extends ObjectSpruceTree {

    private final float leafStartHeightMultiplier;
    private final int baseLeafRadius;
    private final boolean fromSapling;

    public ObjectBigSpruceTree(float leafStartHeightMultiplier, int baseLeafRadius) {
        this(leafStartHeightMultiplier, baseLeafRadius, false);
    }

    public ObjectBigSpruceTree(float leafStartHeightMultiplier, int baseLeafRadius, boolean fromSapling) {
        this.leafStartHeightMultiplier = leafStartHeightMultiplier;
        this.baseLeafRadius = baseLeafRadius;
        this.fromSapling = fromSapling;
    }

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        this.treeHeight = fromSapling ? Utils.rand(15, 20) : random.nextBoundedInt(15) + 20;

        int topSize = this.treeHeight - (int) (this.treeHeight * leafStartHeightMultiplier);
        int lRadius = baseLeafRadius + random.nextBoundedInt(2);

        this.placeTrunk(level, x, y, z, random, this.getTreeHeight() - random.nextBoundedInt(3));

        this.placeLeaves(level, topSize, lRadius, x, y, z, random);
    }

    @Override
    protected void placeTrunk(ChunkManager level, int x, int y, int z, NukkitRandom random, int trunkHeight) {
        // The base dirt block
        level.setBlockAt(x, y - 1, z, Block.DIRT);
        int radius = 2;

        for (int yy = 0; yy < trunkHeight; ++yy) {
            for (int xx = 0; xx < radius; xx++) {
                for (int zz = 0; zz < radius; zz++) {
                    int blockId = level.getBlockIdAt(x, y + yy, z);
                    if (this.overridable(blockId)) {
                        level.setBlockAt(x + xx, y + yy, z + zz, this.getTrunkBlock(), this.getType());
                    }
                }
            }
        }
    }

    @Override
    public void placeLeaves(ChunkManager level, int topSize, int lRadius, int x, int y, int z, NukkitRandom random) {
        int radius = random.nextBoundedInt(2);
        int maxR = 1;
        int minR = 0;

        for (int yy = 0; yy <= topSize; ++yy) {
            int yyy = y + this.treeHeight - yy;

            for (int xx = x - radius; xx <= x + radius + 1; ++xx) {
                for (int zz = z - radius; zz <= z + radius + 1; ++zz) {
                    if (topSize - yy > 1 && radius > 0) {
                        if (Math.abs(xx - x) > radius && Math.abs(zz - z) > radius) {
                            continue;
                        }

                        if (xx - x <= -radius && zz - z <= -radius) {
                            continue;
                        }

                        if (Math.abs(xx - x) > radius && zz - z <= -radius) {
                            continue;
                        }

                        if (xx - x <= -radius && Math.abs(zz - z) > radius) {
                            continue;
                        }
                    }

                    if (!Block.isBlockSolidById(level.getBlockIdAt(xx, yyy, zz))) {
                        level.setBlockAt(xx, yyy, zz, this.getLeafBlock(), this.getType());
                    }
                }
            }

            if (radius >= maxR) {
                radius = minR;
                minR = 1;
                if (++maxR > lRadius) {
                    maxR = lRadius;
                }
            } else {
                ++radius;
            }
        }
    }
}
