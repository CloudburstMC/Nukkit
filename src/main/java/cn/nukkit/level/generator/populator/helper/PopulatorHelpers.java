package cn.nukkit.level.generator.populator.helper;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * @author DaPorkchop_
 */
public final class PopulatorHelpers implements BlockID {

    private static final IntSet nonSolidBlocks = new IntOpenHashSet();

    static {
        PopulatorHelpers.nonSolidBlocks.add(BlockID.AIR);
        PopulatorHelpers.nonSolidBlocks.add(BlockID.LEAVES);
        PopulatorHelpers.nonSolidBlocks.add(BlockID.LEAVES2);
        PopulatorHelpers.nonSolidBlocks.add(BlockID.SNOW_LAYER);
    }

    private PopulatorHelpers() {
    }

    public static boolean canGrassStay(final int x, final int y, final int z, final FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk);
    }

    public static boolean isNonSolid(final int id) {
        return PopulatorHelpers.nonSolidBlocks.contains(id);
    }

}
