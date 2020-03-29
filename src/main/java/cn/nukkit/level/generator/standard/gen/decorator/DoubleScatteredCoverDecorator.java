package cn.nukkit.level.generator.standard.gen.decorator;

import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.filter.BlockFilter;
import cn.nukkit.level.generator.standard.misc.selector.BlockSelector;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import net.daporkchop.lib.random.PRandom;

import static java.lang.Math.*;

/**
 * Similar to {@link ScatteredCoverDecorator}, but places two blocks on top of each other.
 *
 * @author DaPorkchop_
 * @see ScatteredCoverDecorator
 */
@JsonDeserialize
public class DoubleScatteredCoverDecorator implements Decorator {
    public static final Identifier ID = Identifier.fromString("nukkitx:double_scattered_cover");

    @JsonProperty
    protected double chance = Double.NaN;

    @JsonProperty
    protected BlockFilter on;

    @JsonProperty
    protected BlockFilter replace = BlockFilter.REPLACEABLE;

    @JsonProperty
    protected BlockSelector lowBlock;
    @JsonProperty
    protected BlockSelector highBlock;

    @Override
    public void init(long levelSeed, long localSeed, StandardGenerator generator) {
        Preconditions.checkState(this.chance >= 0.0d && this.chance <= 1.0d, "chance must be in range 0-1!");
        Preconditions.checkState(this.on != null, "on must be set!");
        Preconditions.checkState(this.lowBlock != null, "lowBlock must be set!");
        Preconditions.checkState(this.highBlock != null, "highBlock must be set!");
    }

    @Override
    public void decorate(IChunk chunk, PRandom random, int x, int z) {
        BlockFilter on = this.on;
        BlockFilter replace = this.replace;
        double chance = this.chance;
        for (int y = min(chunk.getHighestBlock(x, z), 254); y >= 0; y--) {
            if (random.nextDouble() < chance
                    && on.test(chunk.getBlockRuntimeIdUnsafe(x, y, z, 0))
                    && replace.test(chunk.getBlockRuntimeIdUnsafe(x, y + 1, z, 0))
                    && replace.test(chunk.getBlockRuntimeIdUnsafe(x, y + 2, z, 0))) {
                chunk.setBlockRuntimeIdUnsafe(x, y + 1, z, 0, this.lowBlock.selectRuntimeId(random));
                chunk.setBlockRuntimeIdUnsafe(x, y + 2, z, 0, this.highBlock.selectRuntimeId(random));
            }
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
