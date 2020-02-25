package cn.nukkit.level.generator.standard.gen.noise;

import cn.nukkit.utils.ConfigSection;
import lombok.NonNull;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.random.PRandom;

/**
 * @author DaPorkchop_
 */
public final class VanillaNoiseSource implements NoiseSource {
    private final NoiseSource selector;
    private final NoiseSource low;
    private final NoiseSource high;
    private final NoiseSource randomHeight2d;
    private final NoiseSource height;
    private final NoiseSource volitility;

    public VanillaNoiseSource(@NonNull ConfigSection config, @NonNull PRandom random)   {
        //TODO
    }

    @Override
    public double get(double x) {
        return 0;
    }

    @Override
    public double get(double x, double y) {
        return 0;
    }

    @Override
    public double get(double x, double y, double z) {
        return 0;
    }
}
