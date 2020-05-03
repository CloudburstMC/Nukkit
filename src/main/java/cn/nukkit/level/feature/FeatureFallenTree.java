package cn.nukkit.level.feature;

import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockLog;
import cn.nukkit.block.BlockVine;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.math.BlockFace;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

/**
 * @author DaPorkchop_
 */
public class FeatureFallenTree extends ReplacingWorldFeature {
    protected final IntRange size;
    protected final Identifier logId;
    protected final int logType;
    protected final double vineChance;

    public FeatureFallenTree(@NonNull IntRange size, @NonNull Identifier logId, int logType, double vineChance) {
        this.size = size;
        this.logId = logId;
        this.logType = logType;
        this.vineChance = vineChance;
    }

    @Override
    public boolean place(ChunkManager level, PRandom random, int x, int y, int z) {
        if (y <= 0 || y >= 255) {
            return false;
        }

        final int size = this.size.rand(random);
        final BlockFace direction = BlockFace.Plane.HORIZONTAL.random(random);
        for (int i = 0; i < size; i++) {
            if (!this.test(level.getBlockRuntimeIdUnsafe(x + direction.getXOffset() * i, y, z + direction.getZOffset() * i, 0))
                    || this.testOrLiquid(level.getBlockRuntimeIdUnsafe(x + direction.getXOffset() * i, y - 1, z + direction.getZOffset() * i, 0))) {
                return false;
            }
        }

        level.setBlockAt(x, y, z, 0, this.logId, this.logType);

        int metaDirection = direction.getAxis() == BlockFace.Axis.X ? BlockLog.EAST_WEST : BlockLog.NORTH_SOUTH;
        int log = BlockRegistry.get().getRuntimeId(this.logId, this.logType | metaDirection);
        for (int i = random.nextInt(2) + 2; i < size; i++) {
            level.setBlockRuntimeIdUnsafe(x + direction.getXOffset() * i, y, z + direction.getZOffset() * i, 0, log);

            if (random.nextInt(10) == 0 && this.test(level.getBlockRuntimeIdUnsafe(x + direction.getXOffset() * i, y + 1, z + direction.getZOffset() * i, 0))) {
                level.setBlockAt(x + direction.getXOffset() * i, y + 1, z + direction.getZOffset() * i, 0, random.nextBoolean() ? BlockIds.BROWN_MUSHROOM : BlockIds.RED_MUSHROOM, 0);
            }

            this.replaceGrassWithDirt(level, x + direction.getXOffset() * i, y - 1, z + direction.getZOffset() * i);
        }

        if (this.vineChance > 0.0d) {
            if (random.nextDouble() < this.vineChance && this.test(level.getBlockRuntimeIdUnsafe(x - 1, y, z, 0))) {
                level.setBlockAt(x - 1, y, z, 0, BlockIds.VINE, BlockVine.EAST);
            }
            if (random.nextDouble() < this.vineChance && this.test(level.getBlockRuntimeIdUnsafe(x + 1, y, z, 0))) {
                level.setBlockAt(x + 1, y, z, 0, BlockIds.VINE, BlockVine.WEST);
            }
            if (random.nextDouble() < this.vineChance && this.test(level.getBlockRuntimeIdUnsafe(x, y, z - 1, 0))) {
                level.setBlockAt(x, y, z - 1, 0, BlockIds.VINE, BlockVine.SOUTH);
            }
            if (random.nextDouble() < this.vineChance && this.test(level.getBlockRuntimeIdUnsafe(x, y, z + 1, 0))) {
                level.setBlockAt(x, y, z + 1, 0, BlockIds.VINE, BlockVine.NORTH);
            }
        }

        return true;
    }
}
