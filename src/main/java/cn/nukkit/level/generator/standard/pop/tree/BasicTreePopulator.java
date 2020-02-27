package cn.nukkit.level.generator.standard.pop.tree;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.StandardGeneratorUtils;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.pop.RepeatingPopulator;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.ConfigSection;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.random.PRandom;

import static java.lang.Math.*;

/**
 * A populator that places simple trees, with a similar shape to vanilla oak/birch trees.
 *
 * @author DaPorkchop_
 */
public class BasicTreePopulator extends RepeatingPopulator {
    private final BlockFilter start;
    private final int         trunkId;
    private final int         leafId;
    private final int         minHeight;
    private final int         maxHeight;

    public BasicTreePopulator(@NonNull ConfigSection config, @NonNull PRandom random) {
        super(config, random);

        this.start = StandardGeneratorUtils.parseBlockChecker(config.getString("start"));
        this.trunkId = BlockRegistry.get().getRuntimeId(StandardGeneratorUtils.getBlock(config, "trunk"));
        this.leafId = BlockRegistry.get().getRuntimeId(StandardGeneratorUtils.getBlock(config, "leaf"));
        this.minHeight = PValidation.ensurePositive(config.getInt("minHeight", -1));
        this.maxHeight = PValidation.ensurePositive(config.getInt("maxHeight", this.minHeight) + 1);
    }

    @Override
    protected void tryPopulate(PRandom random, ChunkManager level, int x, int z) {
        int y = 255;

        int id;
        while ((id = level.getBlockRuntimeIdUnsafe(x, y, z, 0)) == 0 && --y >= 0) ;
        if (!this.start.test(id)) {
            //no start block could be found
            return;
        }

        //place trunk
        int height = random.nextInt(this.minHeight, this.maxHeight);
        if (y + height + 2 >= 256) {
            //abort, not enough room
            return;
        }
        for (int i = 0; i < height; i++) {
            level.setBlockRuntimeIdUnsafe(x, ++y, z, 0, this.trunkId);
        }

        //place leaves
        this.placeLeafLayer(random, level, x, z, y - 1, 2, false);
        this.placeLeafLayer(random, level, x, z, y, 2, true);
        this.placeLeafLayer(random, level, x, z, y + 1, 1, false);
        this.placeLeafLayer(random, level, x, z, y + 2, 1, true);
    }

    protected void placeLeafLayer(PRandom random, ChunkManager level, int x, int z, int y, int radius, boolean cutCorners) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (level.getBlockRuntimeIdUnsafe(x + dx, y, z + dz, 0) == 0) {
                    if (cutCorners && abs(dx) == radius && abs(dz) == radius && random.nextBoolean()) {
                        continue;
                    }
                    level.setBlockRuntimeIdUnsafe(x + dx, y, z + dz, 0, this.leafId);
                }
            }
        }
    }
}
