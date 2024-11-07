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
    private static final IntSet nonOceanSolidBlocks = new IntOpenHashSet();

    static {
        nonSolidBlocks.add(AIR);
        nonSolidBlocks.add(LEAVES);
        nonSolidBlocks.add(LEAVES2);
        nonSolidBlocks.add(SNOW_LAYER);
        nonSolidBlocks.add(TALL_GRASS);

        nonOceanSolidBlocks.add(AIR);
        nonOceanSolidBlocks.add(WATER);
        nonOceanSolidBlocks.add(STILL_WATER);
        nonOceanSolidBlocks.add(ICE);
        nonOceanSolidBlocks.add(PACKED_ICE);
        nonOceanSolidBlocks.add(BLUE_ICE);
    }

    private PopulatorHelpers() {
    }

    public static boolean canGrassStay(int x, int y, int z, FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk);
    }

    public static boolean isNonSolid(int id)   {
        return nonSolidBlocks.contains(id);
    }

    public static boolean isNonOceanSolid(int id) {
        return nonOceanSolidBlocks.contains(id);
    }
}
