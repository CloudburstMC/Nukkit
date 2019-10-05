package cn.nukkit.level.generator.populator.helper;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.chunk.Chunk;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * @author DaPorkchop_
 */
public final class PopulatorHelpers implements BlockID {
    private static final IntSet nonSolidBlocks = new IntOpenHashSet();

    static {
        nonSolidBlocks.add(AIR);
        nonSolidBlocks.add(LEAVES);
        nonSolidBlocks.add(LEAVES2);
        nonSolidBlocks.add(SNOW_LAYER);
    }

    private PopulatorHelpers() {
    }

    public static boolean canGrassStay(int x, int y, int z, Chunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk);
    }

    public static boolean isNonSolid(int id)   {
        return nonSolidBlocks.contains(id);
    }
}
