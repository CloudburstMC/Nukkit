package cn.nukkit.level.generator.standard.generation.noise;

import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.NonNull;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.noise.engine.PerlinNoiseEngine;
import net.daporkchop.lib.random.PRandom;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public final class PerlinDefaultNoiseGenerator extends DefaultNoiseGenerator {
    public static final Identifier ID = Identifier.fromString("nukkitx:perlin");

    @Override
    protected NoiseSource create0(@NonNull PRandom random) {
        return new PerlinNoiseEngine(random);
    }
}
