package cn.nukkit.level.generator.standard.generation.noise;

import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.NonNull;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.noise.engine.SimplexNoiseEngine;
import net.daporkchop.lib.random.PRandom;

/**
 * @author DaPorkchop_
 */
@JsonDeserialize
public final class SimplexDefaultNoiseGenerator extends DefaultNoiseGenerator {
    public static final Identifier ID = Identifier.fromString("nukkitx:simplex");

    @Override
    protected NoiseSource create0(@NonNull PRandom random) {
        return new SimplexNoiseEngine(random);
    }
}
