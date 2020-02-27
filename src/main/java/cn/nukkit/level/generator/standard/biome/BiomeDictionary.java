package cn.nukkit.level.generator.standard.biome;

import cn.nukkit.level.generator.standard.store.StandardGeneratorStores;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PValidation;

import java.util.Collections;
import java.util.Map;

/**
 * Mapping of biome identifiers to their numeric IDs.
 *
 * @author DaPorkchop_
 */
public final class BiomeDictionary {
    private final Identifier id;
    private final Object2IntMap<Identifier> biomes = new Object2IntOpenHashMap<>();

    public BiomeDictionary(@NonNull Identifier id, @NonNull ConfigSection config) {
        this.id = id;

        config.<String>getList("inheritsFrom", Collections.emptyList()).stream()
                .map(Identifier::fromString)
                .map(StandardGeneratorStores.biomeDictionary())
                .forEach(dictionary -> this.biomes.putAll(dictionary.biomes));

        for (Map.Entry<String, Object> entry : config.getSection("biomes").entrySet()) {
            this.biomes.put(Identifier.fromString(entry.getKey()), PValidation.ensureNonNegative((Integer) entry.getValue()));
        }
    }

    public int get(@NonNull Identifier id) {
        int runtimeId = this.biomes.getOrDefault(id, -1);
        Preconditions.checkArgument(runtimeId >= 0, "Unknown biome ID \"%s\"", id);
        return runtimeId;
    }

    public Identifier getId() {
        return this.id;
    }
}
