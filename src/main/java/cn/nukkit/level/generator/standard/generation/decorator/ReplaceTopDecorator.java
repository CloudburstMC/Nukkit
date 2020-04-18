package cn.nukkit.level.generator.standard.generation.decorator;

import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.ConstantBlock;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import net.daporkchop.lib.random.PRandom;

import java.util.Objects;

/**
 * Replaces the top block in a chunk with a given replacement if it matches a given filter.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class ReplaceTopDecorator implements Decorator {
    public static final Identifier ID = Identifier.fromString("nukkitx:replace_top");

    @JsonProperty
    protected BlockFilter replace;

    @JsonProperty
    protected IntRange height = IntRange.WHOLE_WORLD;

    @JsonProperty
    protected int block = -1;

    @Override
    public void init(long levelSeed, long localSeed, StandardGenerator generator) {
        Objects.requireNonNull(this.replace, "replace must be set!");
        Objects.requireNonNull(this.height, "height must be set!");
        Preconditions.checkState(this.block >= 0, "block must be set!");
    }

    @Override
    public void decorate(PRandom random, IChunk chunk, int x, int z) {
        int y = chunk.getHighestBlock(x, z);
        if (y >= 0 && this.replace.test(chunk.getBlockRuntimeIdUnsafe(x, y, z, 0)) && this.height.contains(y)) {
            chunk.setBlockRuntimeIdUnsafe(x, y, z, 0, this.block);
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @JsonSetter("block")
    private void setBlock(ConstantBlock block) {
        this.block = block.runtimeId();
    }
}
