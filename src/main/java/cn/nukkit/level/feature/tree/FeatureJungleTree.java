package cn.nukkit.level.feature.tree;

import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockVine;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import cn.nukkit.math.BlockFace;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

/**
 * Generates normal trees, but with vines on the sides.
 *
 * @author DaPorkchop_
 */
public class FeatureJungleTree extends FeatureNormalTree {
    public FeatureJungleTree(@NonNull IntRange height, @NonNull TreeSpecies species) {
        super(height, species);
    }

    public FeatureJungleTree(@NonNull IntRange height, @NonNull BlockSelector wood, @NonNull BlockSelector leaves) {
        super(height, wood, leaves);
    }

    @Override
    protected void placeTrunk(ChunkManager level, PRandom random, int x, int y, int z, int height, int log, int leaves) {
        super.placeTrunk(level, random, x, y, z, height, log, leaves);

        for (int dy = 0; dy < height; dy++) {
            this.placeVines(level, random, x, y + dy, z, BlockFace.NORTH);
            this.placeVines(level, random, x, y + dy, z, BlockFace.SOUTH);
            this.placeVines(level, random, x, y + dy, z, BlockFace.EAST);
            this.placeVines(level, random, x, y + dy, z, BlockFace.WEST);
        }
    }

    protected void placeVines(ChunkManager level, PRandom random, int x, int y, int z, BlockFace face) {
        x -= face.getUnitVector().getX();
        z -= face.getUnitVector().getZ();
        if (random.nextInt(4) != 0 && this.test(level.getBlockRuntimeIdUnsafe(x, y, z, 0))) {
            level.setBlockAt(x, y, z, 0, BlockIds.VINE, BlockVine.getMeta(face));
        }
    }
}
