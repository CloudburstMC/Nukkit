package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.object.BlockList;
import cn.nukkit.math.NukkitRandom;

import java.util.List;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class ObjectBigSpruceTree extends ObjectSpruceTree {
    private final float leafStartHeightMultiplier;
    private final int baseLeafRadius;

    public ObjectBigSpruceTree(float leafStartHeightMultiplier, int baseLeafRadius) {
        this.leafStartHeightMultiplier = leafStartHeightMultiplier;
        this.baseLeafRadius = baseLeafRadius;
    }

    @Override
    public void placeObject(ChunkManager level, List<Block> blocks, int x, int y, int z, NukkitRandom random) {
        this.treeHeight = random.nextBoundedInt(15) + 20;

        int topSize = this.treeHeight - (int) (this.treeHeight * leafStartHeightMultiplier);
        int lRadius = baseLeafRadius + random.nextBoundedInt(2);

        this.placeTrunk(level, blocks, x, y, z, random, this.getTreeHeight() - random.nextBoundedInt(3));
        this.placeLeaves(level, blocks, topSize, lRadius, x, y, z, random);
    }

    @Override
    @Deprecated
    public void placeObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        this.placeObject(level, new BlockList(level), x, y, z, random);
    }

    @Override
    protected void placeTrunk(ChunkManager level, List<Block> blocks, int x, int y, int z, NukkitRandom random, int trunkHeight) {
        // The base dirt block
        level.setBlockAt(x, y - 1, z, Block.DIRT);
        int radius = 2;

        for (int yy = 0; yy < trunkHeight; ++yy) {
            for (int xx = 0; xx < radius; xx++) {
                for (int zz = 0; zz < radius; zz++) {
                    int blockId = level.getBlockIdAt(x, y + yy, z);
                    if (this.overridable(blockId)) {
                        blocks.add(Block.get(this.getTrunkBlock(), this.getType(), new Position(x + xx, y + yy, z + zz)));
                    }
                }
            }
        }
    }

    @Override
    @Deprecated
    protected void placeTrunk(ChunkManager level, int x, int y, int z, NukkitRandom random, int trunkHeight) {
        this.placeTrunk(level, new BlockList(level), x, y, z, random, trunkHeight);
    }
}
