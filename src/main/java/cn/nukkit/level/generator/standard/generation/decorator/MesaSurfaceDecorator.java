package cn.nukkit.level.generator.standard.generation.decorator;

import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.level.generator.standard.generation.noise.NoiseGenerator;
import cn.nukkit.level.generator.standard.misc.ConstantBlock;
import cn.nukkit.level.generator.standard.misc.IntRange;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.NonNull;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.impl.FastPRandom;

import java.util.Arrays;
import java.util.Objects;

import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public class MesaSurfaceDecorator extends DepthNoiseDecorator {
    public static final Identifier ID = Identifier.fromString("nukkitx:mesa_surface");

    protected static final int BAND_COUNT = 64;
    protected static final int BAND_MASK = BAND_COUNT - 1;

    protected int ground = -1;

    @JsonProperty
    protected int seaLevel = -1;

    protected NoiseSource bandOffset;

    protected int[] bands = new int[BAND_COUNT];

    @JsonProperty
    protected ConstantBlock base;

    @JsonProperty("bands")
    protected Band[] layers;

    @JsonProperty("bandOffset")
    protected NoiseGenerator bandOffsetNoise;

    @Override
    protected void init0(long levelSeed, long localSeed, StandardGenerator generator) {
        super.init0(levelSeed, localSeed, generator);

        this.ground = this.ground < 0 ? generator.ground() : this.ground;
        this.seaLevel = this.seaLevel < 0 ? generator.seaLevel() : this.seaLevel;

        PRandom random = new FastPRandom(localSeed);
        Arrays.fill(this.bands, Objects.requireNonNull(this.base, "base must be set!").runtimeId());

        for (Band band : Objects.requireNonNull(this.layers, "bands must be set!")) {
            band.apply(random, this.bands);
        }
        this.layers = null;

        this.bandOffset = Objects.requireNonNull(this.bandOffsetNoise, "bandOffset must be set!").create(random);
        this.bandOffsetNoise = null;
    }

    @Override
    public void decorate(PRandom random, IChunk chunk, int x, int z) {
        final int blockX = (chunk.getX() << 4) + x;
        final int blockZ = (chunk.getZ() << 4) + z;

        final int ground = this.ground;
        final int minHeight = this.seaLevel + this.getDepthNoise(random, blockX, blockZ);

        for (int y = chunk.getHighestBlock(x, z); y >= minHeight; y--) {
            if (chunk.getBlockRuntimeIdUnsafe(x, y, z, 0) == ground) {
                chunk.setBlockRuntimeIdUnsafe(x, y, z, 0, this.getBand(blockX, y, blockZ));
            }
        }
    }

    protected int getBand(int x, int y, int z) {
        return this.bands[(roundI(this.bandOffset.get(x, z)) + y) & BAND_MASK];
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @JsonSetter("ground")
    private void setGround(ConstantBlock block) {
        this.ground = block.runtimeId();
    }

    @JsonDeserialize
    protected static class Band {
        @JsonProperty
        protected IntRange count;

        @JsonProperty
        protected IntRange size = IntRange.ONE;

        @JsonProperty
        protected ConstantBlock block;

        @JsonProperty
        protected ConstantBlock above;

        @JsonProperty
        protected ConstantBlock below;

        public void apply(@NonNull PRandom random, @NonNull int[] bands) {
            Objects.requireNonNull(this.count, "count must be set!");
            Objects.requireNonNull(this.size, "size must be set!");
            Objects.requireNonNull(this.block, "block must be set!");

            for (int id = this.block.runtimeId(), n = this.count.rand(random) - 1; n >= 0; n--) {
                int y = random.nextInt(BAND_COUNT);
                for (int i = 0, size = this.size.rand(random); i < size && y + i < BAND_COUNT; i++) {
                    bands[y + i] = id;

                    if (this.below != null && y + i > 0 && random.nextBoolean()) {
                        bands[y + i - 1] = this.below.runtimeId();
                    }
                    if (this.above != null && y + i < BAND_COUNT - 1 && random.nextBoolean()) {
                        bands[y + i + 1] = this.above.runtimeId();
                    }
                }
            }
        }
    }
}
