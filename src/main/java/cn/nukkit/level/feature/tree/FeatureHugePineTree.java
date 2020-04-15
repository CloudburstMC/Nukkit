package cn.nukkit.level.feature.tree;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * Generates a huge pine tree.
 *
 * Pine trees are identical to spruce trees, but have only a small cluster of leaves near the top.
 *
 * @author DaPorkchop_
 */
public class FeatureHugePineTree extends FeatureHugeSpruceTree {
    public FeatureHugePineTree(@NonNull IntRange height, @NonNull TreeSpecies species) {
        super(height, species);
    }

    public FeatureHugePineTree(@NonNull IntRange height, BlockSelector log, BlockSelector leaves) {
        super(height, log, leaves);
    }

    @Override
    protected int leafHeightOffset(PRandom random, int height) {
        return random.nextInt(5) + 3;
    }
}
