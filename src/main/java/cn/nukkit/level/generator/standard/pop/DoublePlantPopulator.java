package cn.nukkit.level.generator.standard.pop;

import cn.nukkit.block.BlockIds;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
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
public class DoublePlantPopulator extends RepeatingPopulator {
    public static final Identifier ID = Identifier.fromString("nukkitx:double_plant");

    @JsonProperty
    protected BlockFilter on;

    @JsonProperty
    protected BlockFilter replace = BlockFilter.AIR;

    @JsonProperty
    protected int[] types;

    @JsonProperty
    protected int size = 64;

    @Override
    public void init(long levelSeed, long localSeed, StandardGenerator generator) {
        Objects.requireNonNull(this.on, "on must be set!");
        Objects.requireNonNull(this.replace, "replace must be set!");
        Objects.requireNonNull(this.types, "type must be set!");
        Preconditions.checkState(this.size > 0, "size must be at least 1!");

        super.init(levelSeed, localSeed, generator);
    }

    @Override
    protected void tryPopulate(PRandom random, ChunkManager level, int x, int z) {
        int y = random.nextInt(level.getChunk(x >> 4, z >> 4).getHighestBlock(x & 0xF, z & 0xF) << 1);

        final BlockFilter on = this.on;
        final BlockFilter replace = this.replace;

        int type = this.types[random.nextInt(this.types.length)];
        final int bottom = BlockRegistry.get().getRuntimeId(BlockIds.DOUBLE_PLANT, type);
        final int top = BlockRegistry.get().getRuntimeId(BlockIds.DOUBLE_PLANT, type | 0x8);

        for (int i = this.size - 1; i >= 0; i--) {
            int blockY = y + random.nextInt(4) - random.nextInt(4);
            if (blockY < 0 || blockY >= 254) {
                continue;
            }
            int blockX = x + random.nextInt(8) - random.nextInt(8);
            int blockZ = z + random.nextInt(8) - random.nextInt(8);

            IChunk chunk = level.getChunk(blockX >> 4, blockZ >> 4);
            if (on.test(chunk.getBlockRuntimeIdUnsafe(blockX & 0xF, blockY, blockZ & 0xF, 0))
                    && replace.test(chunk.getBlockRuntimeIdUnsafe(blockX & 0xF, blockY + 1, blockZ & 0xF, 0))
                    && replace.test(chunk.getBlockRuntimeIdUnsafe(blockX & 0xF, blockY + 2, blockZ & 0xF, 0))) {
                chunk.setBlockRuntimeIdUnsafe(blockX & 0xF, blockY + 1, blockZ & 0xF, 0, bottom);
                chunk.setBlockRuntimeIdUnsafe(blockX & 0xF, blockY + 2, blockZ & 0xF, 0, top);
            }
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @JsonSetter("type")
    private void setType(int type)  {
        this.types = new int[] { type };
    }
}
