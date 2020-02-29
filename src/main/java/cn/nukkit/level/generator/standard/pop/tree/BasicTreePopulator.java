package cn.nukkit.level.generator.standard.pop.tree;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.misc.ConstantBlock;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.pop.RepeatingPopulator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

import static java.lang.Math.*;

/**
 * A populator that places simple trees, with a similar shape to vanilla oak/birch trees.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class BasicTreePopulator extends RepeatingPopulator {
    @JsonProperty(required = true)
    private BlockFilter   on;
    @JsonProperty(required = true)
    private ConstantBlock trunk;
    @JsonProperty(required = true)
    private ConstantBlock leaf;
    @JsonProperty(required = true)
    private IntRange      height;

    @Override
    protected void tryPopulate(PRandom random, ChunkManager level, int x, int z) {
        int y = 255;

        int id;
        while ((id = level.getBlockRuntimeIdUnsafe(x, y, z, 0)) == 0 && --y >= 0) ;
        if (!this.on.test(id)) {
            //no start block could be found
            return;
        }

        //place trunk
        int height = this.height.rand(random);
        if (y + height + 2 >= 256) {
            //abort, not enough room
            return;
        }
        for (int trunkId = this.trunk.runtimeId(), i = 0; i < height; i++) {
            level.setBlockRuntimeIdUnsafe(x, ++y, z, 0, trunkId);
        }

        //place leaves
        int leafId = this.leaf.runtimeId();
        this.placeLeafLayer(random, level, x, z, y - 1, leafId, 2, false);
        this.placeLeafLayer(random, level, x, z, y + 0, leafId, 2, true);
        this.placeLeafLayer(random, level, x, z, y + 1, leafId, 1, false);
        this.placeLeafLayer(random, level, x, z, y + 2, leafId, 1, true);
    }

    protected void placeLeafLayer(PRandom random, ChunkManager level, int x, int z, int y, int leafId, int radius, boolean cutCorners) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (level.getBlockRuntimeIdUnsafe(x + dx, y, z + dz, 0) == 0) {
                    if (cutCorners && abs(dx) == radius && abs(dz) == radius && random.nextBoolean()) {
                        continue;
                    }
                    level.setBlockRuntimeIdUnsafe(x + dx, y, z + dz, 0, leafId);
                }
            }
        }
    }
}
