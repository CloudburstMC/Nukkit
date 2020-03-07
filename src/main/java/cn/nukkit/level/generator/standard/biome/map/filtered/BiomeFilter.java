package cn.nukkit.level.generator.standard.biome.map.filtered;

import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

import java.util.Collection;

/**
 * A single filter for {@link FilteredBiomeMap}.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize(using = BiomeFilterDeserializer.class)
public interface BiomeFilter {
    void init(long seed, PRandom random);

    GenerationBiome get(int x, int z);
}
