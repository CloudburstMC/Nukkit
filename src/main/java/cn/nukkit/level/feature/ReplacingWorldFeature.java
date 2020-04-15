package cn.nukkit.level.feature;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.math.BlockFace;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;

/**
 * Provides helper methods for other {@link WorldFeature} to quickly check if a block can be replaced.
 *
 * @author DaPorkchop_
 */
public abstract class ReplacingWorldFeature implements WorldFeature, BlockFilter {
    @Override
    public boolean test(Block block) {
        Identifier id = block.getId();
        return id == BlockIds.AIR || id == BlockIds.LEAVES || id == BlockIds.LEAVES2 || (!(block instanceof BlockLiquid) && block.canBeReplaced());
    }

    @Override
    public boolean test(int runtimeId) {
        return runtimeId == 0 || this.test(BlockRegistry.get().getBlock(runtimeId));
    }

    public boolean testOrLiquid(Block block) {
        Identifier id = block.getId();
        return id == BlockIds.AIR || id == BlockIds.LEAVES || id == BlockIds.LEAVES2 || block.canBeReplaced();
    }

    public boolean testOrLiquid(int runtimeId) {
        return runtimeId == 0 || this.testOrLiquid(BlockRegistry.get().getBlock(runtimeId));
    }

    /**
     * Replaces the block at the given coordinates with dirt if it is a grassy block type.
     * <p>
     * The following blocks are considered "grassy":
     * - {@link BlockIds#GRASS}
     * - {@link BlockIds#MYCELIUM}
     * - {@link BlockIds#PODZOL}
     */
    public void replaceGrassWithDirt(ChunkManager level, int x, int y, int z) {
        if (y >= 0 && y < 256) {
            Identifier id = level.getBlockId(x, y, z);
            if (id == BlockIds.GRASS || id == BlockIds.MYCELIUM || id == BlockIds.PODZOL) {
                level.setBlockId(x, y, z, BlockIds.DIRT);
            }
        }
    }

    /**
     * Checks whether all the blocks that horizontally neighbor the given coordinates match the given {@link BlockFilter}.
     */
    public boolean allNeighborsMatch(ChunkManager level, int x, int y, int z, BlockFilter filter) {
        return filter.test(level.getBlockRuntimeIdUnsafe(x - 1, y, z, 0))
                && filter.test(level.getBlockRuntimeIdUnsafe(x + 1, y, z, 0))
                && filter.test(level.getBlockRuntimeIdUnsafe(x, y, z - 1, 0))
                && filter.test(level.getBlockRuntimeIdUnsafe(x, y, z + 1, 0));
    }

    /**
     * Checks whether all the blocks that horizontally neighbor the given coordinates match the given {@link BlockFilter}.
     */
    public boolean allNeighborsMatch(ChunkManager level, int x, int y, int z, BlockFilter filter, BlockFace except) {
        for (BlockFace face : BlockFace.Plane.HORIZONTAL)   {
            if (face != except && !filter.test(level.getBlockRuntimeIdUnsafe(x + face.getXOffset(), y, z + face.getZOffset(), 0)))  {
                return false;
            }
        }
        return true;
    }
}
