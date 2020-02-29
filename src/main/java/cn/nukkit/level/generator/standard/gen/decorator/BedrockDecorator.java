package cn.nukkit.level.generator.standard.gen.decorator;

import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.misc.AbstractGenerationPass;
import cn.nukkit.level.generator.standard.misc.ConstantBlock;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.random.PRandom;

/**
 * Places a given block type using a vanilla bedrock pattern.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public final class BedrockDecorator extends AbstractGenerationPass implements Decorator {
    public static final Identifier ID = Identifier.fromString("nukkitx:bedrock");

    @JsonProperty(required = true)
    private ConstantBlock block;
    @JsonProperty(required = true)
    private IntRange      base;

    @JsonProperty
    private IntRange fade        = IntRange.EMPTY_RANGE;
    @JsonProperty
    private boolean  reverseFade = false;

    @Override
    public void decorate(IChunk chunk, PRandom random, int x, int z) {
        final int runtimeId = this.block.runtimeId();

        for (int y = this.base.min, max = this.base.max; y < max; y++) {
            chunk.setBlockRuntimeIdUnsafe(x, y, z, 0, runtimeId);
        }

        if (!this.fade.empty()) {
            if (this.reverseFade) {
                for (int y = this.fade.max, i = 2, size = this.fade.size() + 2; i < size; y--, i++) {
                    if (random.nextInt(0, i) == 0) {
                        chunk.setBlockRuntimeIdUnsafe(x, y, z, 0, runtimeId);
                    }
                }
            } else {
                for (int y = this.fade.min, i = 2, size = this.fade.size() + 2; i < size; y++, i++) {
                    if (random.nextInt(0, i) == 0) {
                        chunk.setBlockRuntimeIdUnsafe(x, y, z, 0, runtimeId);
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
