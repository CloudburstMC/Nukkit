package cn.nukkit.level.generator.standard.biome;

import cn.nukkit.level.generator.standard.store.StandardGeneratorStores;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Mapping of biome identifiers to their numeric IDs.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonDeserialize
public final class BiomeDictionary {
    private Identifier id;
    @JsonProperty(required = true)
    private Object2IntMap<Identifier> biomes = new Object2IntOpenHashMap<>();

    public int get(@NonNull Identifier id) {
        int runtimeId = this.biomes.getOrDefault(id, -1);
        Preconditions.checkArgument(runtimeId >= 0, "Unknown biome ID \"%s\"", id);
        return runtimeId;
    }

    public Identifier getId() {
        return this.id;
    }

    @JsonSetter("biomes")
    private void setBiomes(BiomesIdMapBuilder builder) {
        this.biomes.putAll(builder.biomes);
    }

    @JsonSetter("inheritFrom")
    private void setInheritFrom(Identifier inheritFrom) {
        this.biomes.putAll(StandardGeneratorStores.biomeDictionary().find(inheritFrom).biomes);
    }

    public synchronized BiomeDictionary setId(@NonNull Identifier id) {
        Preconditions.checkState(this.id == null);
        this.id = id;
        return this;
    }

    @JsonDeserialize
    private static final class BiomesIdMapBuilder {
        private final Object2IntMap<Identifier> biomes = new Object2IntOpenHashMap<>();

        @JsonAnySetter
        private void add(String key, int value) {
            this.biomes.put(Identifier.fromString(key), value);
        }
    }
}
