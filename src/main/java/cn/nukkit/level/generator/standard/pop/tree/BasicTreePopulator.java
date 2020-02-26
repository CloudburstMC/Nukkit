package cn.nukkit.level.generator.standard.pop.tree;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.pop.RepeatingPopulator;
import cn.nukkit.utils.ConfigSection;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

/**
 * A populator that places simple trees, with a similar shape to vanilla oak/birch trees.
 *
 * @author DaPorkchop_
 */
public class BasicTreePopulator extends RepeatingPopulator {
    private final int trunkId;
    private final int leafId;


    public BasicTreePopulator(@NonNull ConfigSection config, @NonNull PRandom random) {
        super(config, random);
    }

    @Override
    protected void tryPopulate(PRandom random, ChunkManager level, int x, int z) {
    }
}
