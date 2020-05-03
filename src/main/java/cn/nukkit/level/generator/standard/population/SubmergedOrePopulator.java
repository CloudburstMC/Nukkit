package cn.nukkit.level.generator.standard.population;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

/**
 * Generates underwater ore veins.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class SubmergedOrePopulator extends ChancePopulator.Column {
    public static final Identifier ID = Identifier.fromString("nukkitx:ore_submerged");

    @JsonProperty
    protected BlockFilter replace;

    @JsonProperty
    protected BlockFilter start;

    @JsonProperty
    protected IntRange radius;

    @JsonProperty
    protected BlockSelector block;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        super.init0(levelSeed, localSeed, generator);

        Objects.requireNonNull(this.replace, "replace must be set!");
        Objects.requireNonNull(this.start, "start must be set!");
        Objects.requireNonNull(this.radius, "radius must be set!");
        Objects.requireNonNull(this.block, "block must be set!");
    }

    @Override
    protected void populate0(PRandom random, ChunkManager level, int blockX, int blockZ) {
        IChunk chunk = level.getChunk(blockX >> 4, blockZ >> 4);
        int y = chunk.getHighestBlock(blockX & 0xF, blockZ & 0xF);
        for (; y > 0; y--) {
            if (this.replace.test(chunk.getBlockRuntimeIdUnsafe(blockX & 0xF, y, blockZ & 0xF, 0))) {
                break;
            }
        }
        if (y <= 0 || y >= 255 || !this.start.test(level.getBlockRuntimeIdUnsafe(blockX, y + 1, blockZ, 0))) {
            return;
        }

        final int radius = this.radius.rand(random);
        final int radiusSq = radius * radius;
        final int block = this.block.selectRuntimeId(random);

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz <= radiusSq) {
                    for (int dy = -1; dy < 1; dy++) {
                        if (this.replace.test(level.getBlockRuntimeIdUnsafe(blockX + dx, y + dy, blockZ + dz, 0))) {
                            level.setBlockRuntimeIdUnsafe(blockX + dx, y + dy, blockZ + dz, 0, block);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
