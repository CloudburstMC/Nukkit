package cn.nukkit.level.generator.standard.store;

import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.cache.Cache;

/**
 * Stores for the various different cacheable resources required for parsing the config for the NukkitX standard generator.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class StandardGeneratorStores {
    private final Cache<BiomeDictionaryStore> BIOME_DICTIONARY_STORE_CACHE = Cache.late(BiomeDictionaryStore::new);
    private final Cache<GenerationBiomeStore> GENERATION_BIOME_STORE_CACHE = Cache.late(GenerationBiomeStore::new);

    public BiomeDictionaryStore biomeDictionary() {
        return BIOME_DICTIONARY_STORE_CACHE.get();
    }

    public GenerationBiomeStore generationBiome() {
        return GENERATION_BIOME_STORE_CACHE.get();
    }
}
