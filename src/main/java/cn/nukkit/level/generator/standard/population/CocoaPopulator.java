package cn.nukkit.level.generator.standard.population;

import cn.nukkit.block.BlockCocoa;
import cn.nukkit.block.BlockIds;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public class CocoaPopulator extends ChancePopulator {
    public static final Identifier ID = Identifier.fromString("nukkitx:cocoa");

    @JsonProperty
    protected IntRange height = IntRange.WHOLE_WORLD;

    @JsonProperty
    protected BlockFilter on;

    @JsonProperty
    protected BlockFilter replace = BlockFilter.AIR;

    public boolean avoidDouble = false;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        super.init0(levelSeed, localSeed, generator);

        Objects.requireNonNull(this.height, "height must be set!");
        Objects.requireNonNull(this.on, "on must be set!");
        Objects.requireNonNull(this.replace, "replace must be set!");
    }

    @Override
    public void populate(PRandom random, ChunkManager level, int blockX, int blockZ) {
        final double chance = this.chance;
        final BlockFilter replace = this.replace;
        final BlockFilter on = this.on;
        final boolean avoidDouble = this.avoidDouble;

        final IChunk chunk = level.getChunk(blockX >> 4, blockZ >> 4);
        for (int y = this.height.min, max = this.height.max; y < max; y++) {
            if (random.nextDouble() >= chance || !replace.test(chunk.getBlockRuntimeIdUnsafe(blockX & 0xF, y, blockZ & 0xF, 0))) {
                continue;
            }

            if (on.test(level.getBlockRuntimeIdUnsafe(blockX - 1, y, blockZ, 0))) {
                if (!avoidDouble || !on.test(level.getBlockRuntimeIdUnsafe(blockX - 2, y, blockZ, 0))) {
                    level.setBlockAt(blockX, y, blockZ, 0, BlockIds.COCOA, BlockCocoa.EAST);
                }
            } else if (on.test(level.getBlockRuntimeIdUnsafe(blockX + 1, y, blockZ, 0))) {
                if (!avoidDouble || !on.test(level.getBlockRuntimeIdUnsafe(blockX + 2, y, blockZ, 0))) {
                    level.setBlockAt(blockX, y, blockZ, 0, BlockIds.COCOA, BlockCocoa.WEST);
                }
            } else if (on.test(level.getBlockRuntimeIdUnsafe(blockX, y, blockZ - 1, 0))) {
                if (!avoidDouble || !on.test(level.getBlockRuntimeIdUnsafe(blockX, y, blockZ - 2, 0))) {
                    level.setBlockAt(blockX, y, blockZ, 0, BlockIds.COCOA, BlockCocoa.SOUTH);
                }
            } else if (on.test(level.getBlockRuntimeIdUnsafe(blockX, y, blockZ + 1, 0))) {
                if (!avoidDouble || !on.test(level.getBlockRuntimeIdUnsafe(blockX, y, blockZ + 2, 0))) {
                    level.setBlockAt(blockX, y, blockZ, 0, BlockIds.COCOA, BlockCocoa.NORTH);
                }
            }
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
