package cn.nukkit.level.generator.standard.generation.decorator;

import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.ConstantBlock;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import net.daporkchop.lib.random.PRandom;

/**
 * Places the surface blocks on terrain, consisting of a single "top" block followed by a number of "filler" blocks.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class SurfaceDecorator extends DepthNoiseDecorator {
    public static final Identifier ID = Identifier.fromString("nukkitx:surface");

    @JsonProperty
    protected IntRange height = null;

    @JsonProperty
    protected int ground = -1;
    @JsonProperty
    protected int cover = -1;
    @JsonProperty
    protected int top = -1;
    @JsonProperty
    protected int filler = -1;

    @JsonProperty
    protected int seaLevel = -1;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        super.init0(levelSeed, localSeed, generator);

        this.ground = this.ground < 0 ? generator.ground() : this.ground;
        Preconditions.checkState(this.top >= 0, "top must be set!");
        Preconditions.checkState(this.filler >= 0, "filler must be set!");

        this.seaLevel = this.seaLevel < 0 ? generator.seaLevel() : this.seaLevel;
    }

    @Override
    public void decorate(PRandom random, IChunk chunk, int x, int z) {
        boolean placed = false;
        final int depth = this.getDepthNoise(chunk, random, x, z);

        final int max = this.height == null ? 255 : this.height.max;
        final int min = this.height == null ? 0 : this.height.min;

        for (int y = chunk.getHighestBlock(x, z); y >= min; y--) {
            if (chunk.getBlockRuntimeIdUnsafe(x, y, z, 0) == this.ground) {
                if (!placed) {
                    placed = true;
                    if (y <= max) {
                        if (y + 1 > this.seaLevel) {
                            if (y < 255 && this.cover >= 0) {
                                chunk.setBlockRuntimeIdUnsafe(x, y + 1, z, 0, this.cover);
                            }
                            chunk.setBlockRuntimeIdUnsafe(x, y--, z, 0, this.top);
                        }
                        for (int i = depth - 1; i >= 0 && y >= 0; i--, y--) {
                            if (chunk.getBlockRuntimeIdUnsafe(x, y, z, 0) == this.ground) {
                                chunk.setBlockRuntimeIdUnsafe(x, y, z, 0, this.filler);
                            } else {
                                //we hit air prematurely, abort!
                                placed = false;
                                break;
                            }
                        }
                    }
                }
            } else {
                //reset when we hit air again
                placed = false;
            }
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @JsonSetter("ground")
    private void setGround(ConstantBlock block) {
        this.ground = block.runtimeId();
    }

    @JsonSetter("cover")
    private void setCover(ConstantBlock block) {
        this.cover = block.runtimeId();
    }

    @JsonSetter("top")
    private void setTop(ConstantBlock block) {
        this.top = block.runtimeId();
    }

    @JsonSetter("filler")
    private void setFiller(ConstantBlock block) {
        this.filler = block.runtimeId();
    }
}
