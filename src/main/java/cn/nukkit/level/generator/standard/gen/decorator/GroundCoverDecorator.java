package cn.nukkit.level.generator.standard.gen.decorator;

import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.ConstantBlock;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

/**
 * Replaces the block above the top block in a chunk with a given replacement.
 * <p>
 * Basically equivalent to the "surface" value in {@link SurfaceDecorator}.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class GroundCoverDecorator implements Decorator {
    public static final Identifier ID = Identifier.fromString("nukkitx:cover");

    @JsonProperty
    protected BlockFilter on;

    @JsonProperty
    protected BlockFilter replace = BlockFilter.AIR;

    @JsonProperty
    protected int cover = -1;

    @Override
    public void init(long levelSeed, long localSeed, StandardGenerator generator) {
        Objects.requireNonNull(this.replace, "replace must be set!");
        Preconditions.checkState(this.cover >= 0, "cover must be set!");
    }

    @Override
    public void decorate(IChunk chunk, PRandom random, int x, int z) {
        int y = chunk.getHighestBlock(x, z);
        if (y >= 0 && y < 255 && (this.on == null || this.on.test(chunk.getBlockRuntimeIdUnsafe(x, y, z, 0)))
                && this.replace.test(chunk.getBlockRuntimeIdUnsafe(x, y + 1, z, 0))) {
            chunk.setBlockRuntimeIdUnsafe(x, y, z, 0, this.cover);
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @JsonSetter("cover")
    private void setCover(ConstantBlock block) {
        this.cover = block.runtimeId();
    }
}
