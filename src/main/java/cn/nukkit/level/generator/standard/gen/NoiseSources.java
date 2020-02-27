package cn.nukkit.level.generator.standard.gen;

import cn.nukkit.level.generator.standard.StandardGeneratorUtils;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.Identifier;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.noise.engine.OpenSimplexNoiseEngine;
import net.daporkchop.lib.noise.engine.PerlinNoiseEngine;
import net.daporkchop.lib.noise.engine.PorkianV2NoiseEngine;
import net.daporkchop.lib.noise.engine.SimplexNoiseEngine;
import net.daporkchop.lib.noise.filter.ScaleOctavesOffsetFilter;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.impl.FastPRandom;

import java.util.function.BiFunction;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class NoiseSources {
    public final Identifier ID_OPENSIMPLEX = Identifier.fromString("nukkitx:opensimplex");
    public final Identifier ID_PERLIN      = Identifier.fromString("nukkitx:perlin");
    public final Identifier ID_PORKIAN     = Identifier.fromString("nukkitx:porkian");
    public final Identifier ID_SIMPLEX     = Identifier.fromString("nukkitx:simplex");

    public final BiFunction<ConfigSection, PRandom, NoiseSource> OPENSIMPLEX = (config, random) -> {
        random = new FastPRandom(StandardGeneratorUtils.computeSeed(random.nextLong(), "noise", config));
        return parse(new OpenSimplexNoiseEngine(random), config);
    };
    public final BiFunction<ConfigSection, PRandom, NoiseSource> PERLIN      = (config, random) -> {
        random = new FastPRandom(StandardGeneratorUtils.computeSeed(random.nextLong(), "noise", config));
        return parse(new PerlinNoiseEngine(random), config);
    };
    public final BiFunction<ConfigSection, PRandom, NoiseSource> PORKIAN     = (config, random) -> {
        random = new FastPRandom(StandardGeneratorUtils.computeSeed(random.nextLong(), "noise", config));
        return parse(new PorkianV2NoiseEngine(random), config);
    };
    public final BiFunction<ConfigSection, PRandom, NoiseSource> SIMPLEX     = (config, random) -> {
        random = new FastPRandom(StandardGeneratorUtils.computeSeed(random.nextLong(), "noise", config));
        return parse(new SimplexNoiseEngine(random), config);
    };

    public NoiseSource parse(@NonNull NoiseSource src, @NonNull ConfigSection config) {
        double scaleX = config.getDouble("scaleX", 1.0d);
        double scaleY = config.getDouble("scaleY", 1.0d);
        double scaleZ = config.getDouble("scaleZ", 1.0d);
        int octaves = PValidation.ensurePositive(config.getInt("octaves", 1));
        double factor = config.getDouble("factor", 1.0d);
        double offset = config.getDouble("offset", 0.0d);

        return new ScaleOctavesOffsetFilter(src, scaleX, scaleY, scaleZ, octaves, factor, offset);
    }
}
