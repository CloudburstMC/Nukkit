package cn.nukkit.level.generator.standard.gen.density;

import cn.nukkit.level.generator.standard.biome.map.BiomeMap;
import cn.nukkit.level.generator.standard.gen.noise.NoiseGenerator;
import cn.nukkit.level.generator.standard.misc.AbstractGenerationPass;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.NonNull;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.impl.FastJavaPRandom;
import net.daporkchop.lib.random.impl.FastPRandom;

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
    private NoiseSource randomHeight2d;
    private NoiseSource height;
    private NoiseSource volatility;

    @JsonProperty(required = true)
    private NoiseGenerator selectorNoise;
    @JsonProperty
    private NoiseGenerator lowNoise;
    @JsonProperty
    private NoiseGenerator highNoise;
    @JsonProperty
    private NoiseGenerator randomNoise;
    @JsonProperty
    private NoiseGenerator heightNoise;
    @JsonProperty
    private NoiseGenerator volatilityNoise;

    @Override
    protected void init0(long levelSeed, long localSeed) {
        PRandom random = new FastPRandom(localSeed);
        this.selector = this.selectorNoise.create(new FastPRandom(random.nextLong()));
        //porktodo: the rest
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
