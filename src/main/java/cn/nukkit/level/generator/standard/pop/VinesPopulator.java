package cn.nukkit.level.generator.standard.pop;

import cn.nukkit.block.BlockIds;
import cn.nukkit.block.BlockVine;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public class VinesPopulator extends ChancePopulator {
    public static final Identifier ID = Identifier.fromString("nukkitx:vines");

    @JsonProperty
    protected IntRange height = IntRange.WHOLE_WORLD;

    @JsonProperty
    protected BlockFilter on;

    @JsonProperty
    protected BlockFilter replace = BlockFilter.AIR;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        Objects.requireNonNull(this.height, "height must be set!");
        Objects.requireNonNull(this.on, "on must be set!");
        Objects.requireNonNull(this.replace, "replace must be set!");

        super.init(levelSeed, localSeed, generator);
    }

    @Override
    public void populate(PRandom random, ChunkManager level, int blockX, int blockZ) {
        final double chance = this.chance;
        final BlockFilter replace = this.replace;
        final BlockFilter on = this.on;

        final IChunk chunk = level.getChunk(blockX >> 4, blockZ >> 4);
        for (int y = this.height.min, max = this.height.max; y < max; y++) {
            if (random.nextDouble() >= chance || !replace.test(chunk.getBlockRuntimeIdUnsafe(blockX & 0xF, y, blockZ & 0xF, 0))) {
                continue;
            }

            int meta = 0;
            if (on.test(level.getBlockRuntimeIdUnsafe(blockX - 1, y, blockZ, 0))) {
                meta |= BlockVine.WEST;
            }
            if (on.test(level.getBlockRuntimeIdUnsafe(blockX + 1, y, blockZ, 0))) {
                meta |= BlockVine.EAST;
            }
            if (on.test(level.getBlockRuntimeIdUnsafe(blockX, y, blockZ - 1, 0))) {
                meta |= BlockVine.NORTH;
            }
            if (on.test(level.getBlockRuntimeIdUnsafe(blockX, y, blockZ + 1, 0))) {
                meta |= BlockVine.SOUTH;
            }

            if (meta != 0) {
                chunk.setBlock(blockX & 0xF, y, blockZ & 0xF, 0, BlockRegistry.get().getBlock(BlockIds.VINE, meta));
            }
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
