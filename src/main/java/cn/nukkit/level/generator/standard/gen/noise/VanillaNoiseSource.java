package cn.nukkit.level.generator.standard.gen.noise;

import cn.nukkit.level.generator.standard.gen.NoiseSourceFactory;
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
public final class VanillaNoiseSource implements NoiseSource {
    private final NoiseSource selector;
    private final NoiseSource low;
    private final NoiseSource high;
    private final NoiseSource randomHeight2d;
    private final NoiseSource height;
    private final NoiseSource volitility;

    public VanillaNoiseSource(@NonNull ConfigSection config, @NonNull PRandom random) {
        NoiseSourceFactory registry = StandardGeneratorRegistries.noiseSource();
        this.selector = registry.apply(config.getSection("selectorNoise"), random);
        this.low = registry.apply(config.getSection("low"), random);
        this.high = registry.apply(config.getSection("high"), random);
        this.randomHeight2d = registry.apply(config.getSection("randomHeight2d"), random);
        this.height = registry.apply(config.getSection("height"), random);
        this.volitility = registry.apply(config.getSection("volitility"), random);
    }

    @Override
    public double get(double x, double y, double z) {
        return this.selector.get(x, y, z) - (1.0d / 32.0d) * y;
    }

    @Override
    public double get(double x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double get(double x, double y) {
        throw new UnsupportedOperationException();
    }
}
