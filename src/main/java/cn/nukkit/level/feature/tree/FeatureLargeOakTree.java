package cn.nukkit.level.feature.tree;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

/**
 * Generates either a normal tree or a large oak tree.
 *
 * @author DaPorkchop_
 */
public class FeatureLargeOakTree extends FeatureNormalTree {
    public static final IntRange DEFAULT_HEIGHT = new IntRange(7, 12);

    protected final double chance;

    protected final IntRange largeHeight;

    public FeatureLargeOakTree(@NonNull IntRange normalHeight, @NonNull TreeSpecies species, double chance, @NonNull IntRange largeHeight) {
        super(normalHeight, species);

        this.chance = chance;
        this.largeHeight = largeHeight;
    }

    public FeatureLargeOakTree(@NonNull IntRange normalHeight, @NonNull BlockSelector wood, @NonNull BlockSelector leaves, double chance, @NonNull IntRange largeHeight) {
        super(normalHeight, wood, leaves);

        this.chance = chance;
        this.largeHeight = largeHeight;
    }

    @Override
    public boolean place(ChunkManager level, PRandom random, int x, int y, int z) {
        if (random.nextDouble() >= this.chance) {
            return super.place(level, random, x, y, z);
        }

        //TODO: actually generate large oak tree
        return super.place(level, random, x, y, z);
    }
}
