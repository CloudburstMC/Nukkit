package cn.nukkit.level.generator.standard.gen.decorator;

import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.misc.ConstantBlock;
import cn.nukkit.level.generator.standard.misc.HeightRange;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

/**
 * Places a given block type using a vanilla bedrock pattern.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public final class BedrockDecorator implements Decorator {
    @JsonProperty(required = true)
    private ConstantBlock block;
    @JsonProperty(required = true)
    private HeightRange   base;

    @JsonProperty
    private HeightRange fade        = HeightRange.EMPTY_RANGE;
    @JsonProperty
    private boolean     reverseFade = false;

    @Override
    public void decorate(IChunk chunk, PRandom random, int x, int z) {
        final int runtimeId = this.block.runtimeId();

        for (int y = this.base.minY, max = this.base.maxY; y < max; y++) {
            chunk.setBlockRuntimeIdUnsafe(x, y, z, 0, runtimeId);
        }

        if (!this.fade.empty()) {
            if (this.reverseFade) {
                for (int y = this.fade.maxY, i = 2, size = this.fade.size() + 2; i < size; y--, i++) {
                    if (random.nextInt(0, i) == 0) {
                        chunk.setBlockRuntimeIdUnsafe(x, y, z, 0, runtimeId);
                    }
                }
            } else {
                for (int y = this.fade.minY, i = 2, size = this.fade.size() + 2; i < size; y++, i++) {
                    if (random.nextInt(0, i) == 0) {
                        chunk.setBlockRuntimeIdUnsafe(x, y, z, 0, runtimeId);
                    }
                }
            }
        }
    }
}
