package cn.nukkit.level.generator.standard.biome;

import cn.nukkit.level.generator.standard.store.StandardGeneratorStores;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.Identifier;
import lombok.NonNull;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Private registry for biomes, created separately for each implementation
 *
 * @author DaPorkchop_
 */
public final class BiomeDictionary {
    private final Identifier id;
    private final Map<Identifier, GenerationBiome> biomes = new IdentityHashMap<>();

    public BiomeDictionary(@NonNull Identifier id, @NonNull ConfigSection config) {
        this.id = id;

        config.<String>getList("inheritsFrom", Collections.emptyList()).stream()
                .map(Identifier::fromString)
                .map(StandardGeneratorStores.biomeDictionary())
                .forEach(dictionary -> this.biomes.putAll(dictionary.biomes));

        config.<ConfigSection>getList("biomes", Collections.emptyList()).stream()
                .map(GenerationBiome::new)
                .forEach(biome -> this.biomes.put(biome.getId(), biome));
    }

    public GenerationBiome find(@NonNull Identifier id) {
        return this.biomes.get(id);
    }
}
