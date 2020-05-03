package cn.nukkit.level.generator.standard.population;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import cn.nukkit.level.generator.standard.population.cluster.AbstractReplacingPopulator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public class GlowstonePopulator extends AbstractReplacingPopulator {
    public static final Identifier ID = Identifier.fromString("nukkitx:glowstone");

    @JsonProperty
    protected IntRange height = IntRange.WHOLE_WORLD;

    @JsonProperty
    protected BlockSelector block;

    @JsonProperty
    protected int tries = 0;

    protected int ground;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        super.init0(levelSeed, localSeed, generator);

        Objects.requireNonNull(this.height, "height must be set!");
        Objects.requireNonNull(this.block, "block must be set!");
        Preconditions.checkState(this.tries > 0, "tries must be set!");

        this.ground = generator.ground();
    }

    @Override
    protected void populate0(PRandom random, ChunkManager level, int blockX, int blockZ) {
        final BlockFilter replace = this.replace;
        final int blockY = this.height.rand(random);
        final int block = this.block.selectRuntimeId(random);
        final int ground = this.ground;

        if (blockY >= 255 || !replace.test(level.getBlockRuntimeIdUnsafe(blockX, blockY, blockZ, 0)) || level.getBlockRuntimeIdUnsafe(blockX, blockY + 1, blockZ, 0) != ground) {
            return;
        }
        level.setBlockRuntimeIdUnsafe(blockX, blockY, blockZ, 0, block);

        for (int i = this.tries - 1; i >= 0; i--) {
            int x = blockX + random.nextInt(8) - random.nextInt(8);
            int y = blockY - random.nextInt(12);
            int z = blockZ + random.nextInt(8) - random.nextInt(8);

            if (replace.test(level.getBlockRuntimeIdUnsafe(x, y, z, 0))) {
                int neighbors = 0;
                if (level.getBlockRuntimeIdUnsafe(x - 1, y, z, 0) == block) {
                    neighbors++;
                }
                if (level.getBlockRuntimeIdUnsafe(x + 1, y, z, 0) == block) {
                    neighbors++;
                }
                if (level.getBlockRuntimeIdUnsafe(x, y - 1, z, 0) == block) {
                    neighbors++;
                }
                if (level.getBlockRuntimeIdUnsafe(x, y + 1, z, 0) == block) {
                    neighbors++;
                }
                if (level.getBlockRuntimeIdUnsafe(x, y, z - 1, 0) == block) {
                    neighbors++;
                }
                if (level.getBlockRuntimeIdUnsafe(x, y, z + 1, 0) == block) {
                    neighbors++;
                }

                if (neighbors == 1) {
                    level.setBlockRuntimeIdUnsafe(x, y, z, 0, block);
                }
            }
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
