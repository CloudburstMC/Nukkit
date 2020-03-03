package cn.nukkit.level.generator.standard.gen.density;

import cn.nukkit.level.generator.standard.biome.BiomeTerrainCache;
import cn.nukkit.level.generator.standard.biome.map.BiomeMap;
import cn.nukkit.level.generator.standard.gen.noise.NoiseGenerator;
import cn.nukkit.level.generator.standard.misc.AbstractGenerationPass;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.NonNull;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.impl.FastPRandom;

import static java.util.Objects.*;

/**
 * A {@link NoiseSource} that provides noise similar to that of vanilla terrain.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public final class VanillaDensitySource extends AbstractGenerationPass implements DensitySource {
    public static final Identifier ID = Identifier.fromString("nukkitx:vanilla");

    private NoiseSource selector;
    private NoiseSource low;
    private NoiseSource high;
    private NoiseSource depth;

    private final BiomeTerrainCache terrainCache = new BiomeTerrainCache(2);

    @JsonProperty(required = true)
    private NoiseGenerator selectorNoise;
    @JsonProperty(required = true)
    private NoiseGenerator lowNoise;
    @JsonProperty(required = true)
    private NoiseGenerator highNoise;
    @JsonProperty(required = true)
    private NoiseGenerator depthNoise;

    @Override
    protected void init0(long levelSeed, long localSeed) {
        PRandom random = new FastPRandom(localSeed);
        this.selector = requireNonNull(this.selectorNoise, "selectorNoise must be set!").create(new FastPRandom(random.nextLong()));
        this.low = requireNonNull(this.lowNoise, "lowNoise must be set!").create(new FastPRandom(random.nextLong()));
        this.high = requireNonNull(this.highNoise, "highNoise must be set!").create(new FastPRandom(random.nextLong()));
        this.depth = requireNonNull(this.depthNoise, "depthNoise must be set!").create(new FastPRandom(random.nextLong()));
    }

    @Override
    public double get(int x, int y, int z, @NonNull BiomeMap biomes) {
        return this.selector.get((double) x, (double) y, (double) z) - (1.0d / 32.0d) * y;
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
