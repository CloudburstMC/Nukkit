package cn.nukkit.level.generator.standard.population.plant;

import cn.nukkit.block.BlockIds;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

/**
 * Places patches of double plants in the world.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class DoublePlantPopulator extends AbstractPlantPopulator {
    public static final Identifier ID = Identifier.fromString("nukkitx:double_plant");

    @JsonProperty
    protected int[] types;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        super.init0(levelSeed, localSeed, generator);

        Objects.requireNonNull(this.types, "type must be set!");
    }

    @Override
    protected void placeCluster(PRandom random, ChunkManager level, int x, int y, int z) {
        final BlockFilter on = this.on;
        final BlockFilter replace = this.replace;

        int type = this.types[random.nextInt(this.types.length)];
        final int bottom = BlockRegistry.get().getRuntimeId(BlockIds.DOUBLE_PLANT, type);
        final int top = BlockRegistry.get().getRuntimeId(BlockIds.DOUBLE_PLANT, type | 0x8);

        for (int i = this.patchSize - 1; i >= 0; i--) {
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
    private void setType(int type) {
        this.types = new int[]{type};
    }
}
