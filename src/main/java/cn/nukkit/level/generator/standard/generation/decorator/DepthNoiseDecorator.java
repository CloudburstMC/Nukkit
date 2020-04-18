package cn.nukkit.level.generator.standard.generation.decorator;

import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.misc.AbstractGenerationPass;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.noise.engine.SimplexNoiseEngine;
import net.daporkchop.lib.noise.filter.ScaleOctavesOffsetFilter;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.impl.FastPRandom;

import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public abstract class DepthNoiseDecorator extends AbstractGenerationPass implements Decorator {
    //depth noise is not bound to world seed
    public static final NoiseSource DEPTH_NOISE = new SimplexNoiseEngine(new FastPRandom(0xDEADBEEF00001337L));

    protected NoiseSource depthNoise;

    @JsonProperty
    @JsonAlias({
            "depthRandom",
            "random"
    })
    protected double randomFactor = 0.25d;

    @JsonProperty
    @JsonAlias({
            "depthScale",
            "scale"
    })
    protected double depthNoiseScale = 0.0078125d; // 0.0625 / (1 << 3)

    @JsonProperty
    @JsonAlias({
            "depthFactor",
            "factor"
    })
    protected double depthNoiseFactor = 0.3333333333d;

    @JsonProperty
    @JsonAlias({
            "depthOffset",
            "offset"
    })
    protected double depthNoiseOffset = 3.0d;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        this.depthNoise = new ScaleOctavesOffsetFilter(DEPTH_NOISE, this.depthNoiseScale, this.depthNoiseScale, 0.0d, 4, this.depthNoiseFactor, this.depthNoiseOffset);
    }

    protected int getDepthNoise(IChunk chunk, PRandom random, int x, int z) {
        return this.getDepthNoise(random, (chunk.getX() << 4) + x, (chunk.getZ() << 4) + z);
    }

    protected int getDepthNoise(PRandom random, int x, int z) {
        return roundI(this.depthNoise.get(x, z) + random.nextDouble() * this.randomFactor);
    }
}
