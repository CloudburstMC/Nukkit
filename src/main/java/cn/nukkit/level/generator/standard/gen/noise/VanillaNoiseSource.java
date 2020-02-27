package cn.nukkit.level.generator.standard.gen.noise;

import cn.nukkit.level.generator.standard.biome.map.BiomeMap;
import cn.nukkit.level.generator.standard.gen.DensitySource;
import cn.nukkit.level.generator.standard.registry.NoiseSourceRegistry;
import cn.nukkit.level.generator.standard.registry.StandardGeneratorRegistries;
import cn.nukkit.utils.ConfigSection;
import lombok.NonNull;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.random.PRandom;

/**
 * A {@link NoiseSource} that provides noise similar to that of vanilla terrain.
 *
 * @author DaPorkchop_
 */
public final class VanillaNoiseSource implements DensitySource {
    private final NoiseSource selector;
    private final NoiseSource low;
    private final NoiseSource high;
    private final NoiseSource randomHeight2d;
    private final NoiseSource height;
    private final NoiseSource volitility;

    public VanillaNoiseSource(@NonNull ConfigSection config, @NonNull PRandom random) {
        NoiseSourceRegistry registry = StandardGeneratorRegistries.noiseSource();
        this.selector = registry.apply(config.getSection("selectorNoise"), random);
        this.low = null;//registry.apply(config.getSection("low"), random);
        this.high = null;//registry.apply(config.getSection("high"), random);
        this.randomHeight2d = null;//registry.apply(config.getSection("randomHeight2d"), random);
        this.height = null;//registry.apply(config.getSection("height"), random);
        this.volitility = null;//registry.apply(config.getSection("volitility"), random);
    }

    @Override
    public double get(int x, int y, int z, @NonNull BiomeMap biomes) {
        return this.selector.get((double) x, (double) y, (double) z) - (1.0d / 32.0d) * y;
    }
}
