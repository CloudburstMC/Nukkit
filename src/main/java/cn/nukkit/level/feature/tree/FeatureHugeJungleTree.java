package cn.nukkit.level.feature.tree;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

import static java.lang.Math.*;
import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * @author DaPorkchop_
 */
public class FeatureHugeJungleTree extends FeatureHugeTree {
    public static final IntRange DEFAULT_HEIGHT = new IntRange(12, 31);

    public FeatureHugeJungleTree(@NonNull IntRange height, @NonNull TreeSpecies species) {
        super(height, species);
    }

    public FeatureHugeJungleTree(@NonNull IntRange height, BlockSelector log, BlockSelector leaves) {
        super(height, log, leaves);
    }

    @Override
    protected void placeLeaves(ChunkManager level, PRandom random, int x, int y, int z, int height, int log, int leaves) {
        for (int dy = -2; dy <= 0; dy++) {
            this.placeCircularLeafLayer(level, x, y + height + dy, z, 3 - dy, leaves);
        }
    }

    @Override
    protected void placeTrunk(ChunkManager level, PRandom random, int x, int y, int z, int height, int log, int leaves) {
        super.placeTrunk(level, random, x, y, z, height, log, leaves);

        //branches
        for (int dy = height - 2 - random.nextInt(4), limit = height >> 1; dy > limit; dy -= 2 + random.nextInt(4)) {
            double dir = random.nextDouble() * Math.PI * 2.0d;
            double dirCos = cos(dir);
            double dirSin = sin(dir);

            int dx = 0;
            int dz = 0;

            for (int branchLength = 0; branchLength < 5; branchLength++)    {
                dx = floorI(1.5d + dirCos * branchLength);
                dz = floorI(1.5d + dirSin * branchLength);
                int ddy = (branchLength >> 1) - 3;
                if (this.test(level.getBlockRuntimeIdUnsafe(x + dx, y + dy + ddy, z + dz, 0)))  {
                    level.setBlockRuntimeIdUnsafe(x + dx, y + dy + ddy, z + dz, 0, log);
                }
            }

            for (int ddy = -(1 + random.nextInt(2)); ddy <= 0; ddy++) {
                this.placeCircularLeafLayer(level, x + dx, y + dy + ddy, z + dz, 1 - ddy, leaves);
            }
        }
    }
}
