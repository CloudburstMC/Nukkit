package cn.nukkit.level.generator.standard.pop;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

/**
 * Places patches of plants of varying height in the world.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class ShrubPopulator extends RepeatingPopulator {
    public static final Identifier ID = Identifier.fromString("nukkitx:shrub");

    @JsonProperty
    protected BlockFilter on;

    @JsonProperty
    protected BlockFilter replace = BlockFilter.AIR;

    @JsonProperty
    @JsonAlias({"types", "block", "blocks"})
    protected BlockSelector type;

    @JsonProperty
    protected int size = 64;

    @Override
    public void init(long levelSeed, long localSeed, StandardGenerator generator) {
        Objects.requireNonNull(this.on, "on must be set!");
        Objects.requireNonNull(this.replace, "replace must be set!");
        Objects.requireNonNull(this.type, "type must be set!");
        Preconditions.checkState(this.size > 0, "size must be at least 1!");

        super.init(levelSeed, localSeed, generator);
    }

    @Override
    protected void tryPopulate(PRandom random, ChunkManager level, int x, int z) {
        int y = random.nextInt(level.getChunk(x >> 4, z >> 4).getHighestBlock(x & 0xF, z & 0xF) << 1);

        final BlockFilter on = this.on;
        final BlockFilter replace = this.replace;
        final int type = this.type.selectRuntimeId(random);

        for (int i = this.size - 1; i >= 0; i--) {
            int blockY = y + random.nextInt(4) - random.nextInt(4);
            if (blockY < 0 || blockY >= 255) {
                continue;
            }
            int blockX = x + random.nextInt(8) - random.nextInt(8);
            int blockZ = z + random.nextInt(8) - random.nextInt(8);

            IChunk chunk = level.getChunk(blockX >> 4, blockZ >> 4);
            if (on.test(chunk.getBlockRuntimeIdUnsafe(blockX & 0xF, blockY, blockZ & 0xF, 0))
                    && replace.test(chunk.getBlockRuntimeIdUnsafe(blockX & 0xF, blockY + 1, blockZ & 0xF, 0))) {
                chunk.setBlockRuntimeIdUnsafe(blockX & 0xF, blockY + 1, blockZ & 0xF, 0, type);
            }
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
