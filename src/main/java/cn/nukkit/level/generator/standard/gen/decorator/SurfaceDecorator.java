package cn.nukkit.level.generator.standard.gen.decorator;

import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.ConstantBlock;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.noise.engine.SimplexNoiseEngine;
import net.daporkchop.lib.noise.filter.ScaleOctavesOffsetFilter;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.impl.FastPRandom;

import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * Places the surface blocks on terrain, consisting of a single "top" block followed by a number of "filler" blocks.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public class SurfaceDecorator implements Decorator {
    public static final Identifier ID = Identifier.fromString("nukkitx:surface");

    //depth noise is not bound to world seed
    public static final NoiseSource DEPTH_NOISE = new SimplexNoiseEngine(new FastPRandom(0xDEADBEEF00001337L));

    protected NoiseSource depthNoise;

    @JsonProperty
    protected int cover = -1;
    @JsonProperty
    protected int ground = -1;
    @JsonProperty
    protected int top    = -1;
    @JsonProperty
    @JsonAlias({"fill"})
    protected int filler = -1;

    protected int seaLevel;

    @JsonProperty
    @JsonAlias({"depthScale", "scale"})
    protected double depthNoiseScale = 0.0078125d;

    @JsonProperty
    @JsonAlias({"depthFactor", "factor"})
    protected double depthNoiseFactor = 1.5d;

    @JsonProperty
    @JsonAlias({"depthOffset", "offset"})
    protected double depthNoiseOffset = 5.0d;

    @Override
    public void init(long levelSeed, long localSeed, StandardGenerator generator) {
        Preconditions.checkState(this.ground >= 0, "ground must be set!");
        Preconditions.checkState(this.top >= 0, "top must be set!");
        Preconditions.checkState(this.filler >= 0, "filler must be set!");

        this.depthNoise = new ScaleOctavesOffsetFilter(DEPTH_NOISE, this.depthNoiseScale, this.depthNoiseScale, 0.0d, 4, this.depthNoiseFactor, this.depthNoiseOffset);
        this.seaLevel = generator.seaLevel();
    }

    @Override
    public void decorate(IChunk chunk, PRandom random, int x, int z) {
        boolean placed = false;
        int depth = roundI(this.depthNoise.get(x + (chunk.getX() << 4), z + (chunk.getZ() << 4)));

        for (int y = 255; y >= 0; y--) {
            if (chunk.getBlockRuntimeIdUnsafe(x, y, z, 0) == this.ground) {
                if (!placed) {
                    placed = true;
                    if (y + 1 >= this.seaLevel) {
                        if (y < 255 && this.cover >= 0)    {
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

    @JsonSetter("cover")
    private void setCover(ConstantBlock block) {
        this.cover = block.runtimeId();
    }

    @JsonSetter("ground")
    private void setGround(ConstantBlock block) {
        this.ground = block.runtimeId();
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
