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

import java.util.Objects;

/**
 * Variant of {@link SurfaceDecorator} that adds an additional layer of blocks under the main layers.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class DeepSurfaceDecorator extends SurfaceDecorator {
    public static final Identifier ID = Identifier.fromString("nukkitx:surface_deep");

    @JsonProperty
    protected int deep = -1;

    @JsonProperty
    protected IntRange deepSize;

    @Override
    public void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        super.init0(levelSeed, localSeed, generator);

        Preconditions.checkState(this.deep >= 0, "deep must be set!");
        Objects.requireNonNull(this.deepSize, "deepSize must be set!");
    }

    @Override
    public void decorate(PRandom random, IChunk chunk, int x, int z) {
        boolean placed = false;
        final int depth = this.getDepthNoise(chunk, random, x, z);

        for (int y = 255; y >= 0; y--) {
            if (chunk.getBlockRuntimeIdUnsafe(x, y, z, 0) == this.ground) {
                PLACE:
                if (!placed) {
                    placed = true;
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
                            break PLACE;
                        }
                    }
                    for (int i = this.deepSize.rand(random); i >= 0 && y >= 0; i--, y--) {
                        if (chunk.getBlockRuntimeIdUnsafe(x, y, z, 0) == this.ground) {
                            chunk.setBlockRuntimeIdUnsafe(x, y, z, 0, this.deep);
                        } else {
                            //we hit air prematurely, abort!
                            placed = false;
                            break;
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

    @JsonSetter("deep")
    private void setDeep(ConstantBlock block) {
        this.deep = block.runtimeId();
    }
}
