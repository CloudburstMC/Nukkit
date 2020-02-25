package cn.nukkit.level.generator.standard.biome;

import cn.nukkit.level.generator.standard.StandardGeneratorUtils;
import cn.nukkit.level.generator.standard.store.StandardGeneratorStores;
import cn.nukkit.utils.ConfigSection;
import lombok.NonNull;
import net.daporkchop.lib.random.PRandom;

/**
 * Implementation of {@link BiomeMap} which returns a constant biome.
 *
 * @author DaPorkchop_
 */
public final class ConstantBiomeMap implements BiomeMap {
    private final GenerationBiome biome;

    public ConstantBiomeMap(@NonNull ConfigSection config, @NonNull PRandom random) {
        BiomeDictionary dictionary = StandardGeneratorStores.biomeDictionary().apply(StandardGeneratorUtils.getId(config, "dictionary"));
        this.biome = dictionary.find(StandardGeneratorUtils.getId(config, "biome"));
    }

    @Override
    public GenerationBiome get(int x, int z) {
        return this.biome;
    }
}
