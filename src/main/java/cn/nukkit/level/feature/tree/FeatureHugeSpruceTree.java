package cn.nukkit.level.feature.tree;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * Generates a huge spruce tree.
 *
 * @author DaPorkchop_
 */
public class FeatureHugeSpruceTree extends FeatureHugeTree {
    public static final IntRange DEFAULT_HEIGHT = new IntRange(13, 28);

    public FeatureHugeSpruceTree(@NonNull IntRange height, @NonNull TreeSpecies species) {
        super(height, species);
    }

    public FeatureHugeSpruceTree(@NonNull IntRange height, BlockSelector log, BlockSelector leaves) {
        super(height, log, leaves);
    }

    @Override
    protected void placeLeaves(ChunkManager level, PRandom random, int x, int y, int z, int height, int log, int leaves) {
        int heightOffset = this.leafHeightOffset(random, height);
        int lastRadius = 0;
        for (int dy = height - heightOffset; dy <= height; dy++) {
            int radius = floorI(((double) (height - dy) / (double) heightOffset) * 3.5d);
            this.placeCircularLeafLayer(level, x, y + dy, z, radius + (dy > 0 && radius == lastRadius && ((y + dy) & 1) == 0 ? 1 : 0) + 1, leaves);
            lastRadius = radius;
        }
    }

    protected int leafHeightOffset(PRandom random, int height) {
        return random.nextInt(5) + this.height.min;
    }
}
