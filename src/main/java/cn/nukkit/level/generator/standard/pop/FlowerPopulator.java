package cn.nukkit.level.generator.standard.pop;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

import static cn.nukkit.math.NukkitMath.clamp;

/**
 * Places patches of flowers in the world.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class FlowerPopulator extends RepeatingPopulator {
    public static final Identifier ID = Identifier.fromString("nukkitx:flower");

    @JsonProperty
    protected BlockFilter on;

    @JsonProperty
    protected BlockFilter replace = BlockFilter.AIR;

    @JsonProperty
    @JsonAlias({"type", "block", "blocks"})
    protected BlockSelector types;

    @JsonProperty
    protected IntRange height = IntRange.WHOLE_WORLD;

    @Override
    protected void tryPopulate(PRandom random, ChunkManager level, int x, int z) {
        int y = this.height.rand(random);

        BlockFilter on = this.on;
        BlockFilter replace = this.replace;
        int type = this.types.selectRuntimeId(random);

        for (int i = 0; i < 64; i++) {
            int blockX = x + random.nextInt(8) - random.nextInt(8);
            int blockY = clamp(y + random.nextInt(4) - random.nextInt(4), 1, 255);
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
