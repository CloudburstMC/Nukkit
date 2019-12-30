package cn.nukkit.level.generator.populator.helper;

import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.utils.Identifier;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import static cn.nukkit.block.BlockIds.*;

/**
 * @author DaPorkchop_
 */
public final class PopulatorHelpers {
    private static final Set<Identifier> nonSolidBlocks = Collections.newSetFromMap(new IdentityHashMap<>());

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

    public static boolean isNonSolid(Identifier id) {
        return nonSolidBlocks.contains(id);
    }
}
