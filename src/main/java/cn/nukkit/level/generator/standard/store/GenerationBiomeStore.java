package cn.nukkit.level.generator.standard.store;

import cn.nukkit.Nukkit;
import cn.nukkit.level.generator.standard.StandardGeneratorUtils;
import cn.nukkit.level.generator.standard.biome.BiomeDictionary;
import cn.nukkit.level.generator.standard.biome.BiomeElevation;
import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.gen.decorator.Decorator;
import cn.nukkit.level.generator.standard.pop.Populator;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Store for {@link GenerationBiome}.
 *
 * @author DaPorkchop_
 * @see StandardGeneratorStores#generationBiome()
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class GenerationBiomeStore extends AbstractGeneratorStore<GenerationBiome> {
    public synchronized Collection<GenerationBiome> snapshot() {
        return new HashSet<>(this.idToValues.values());
    }

    public synchronized Collection<GenerationBiome> reset() {
        Collection<GenerationBiome> biomes = new HashSet<>(this.idToValues.values());
        this.idToValues.clear();
        return biomes;
    }

    @Override
    protected GenerationBiome compute(@NonNull Identifier id) throws IOException {
        try (InputStream in = StandardGeneratorUtils.read("biome", id)) {
            return Nukkit.YAML_MAPPER.readValue(in, TempBiome.class).build(id, this.idToValues.size());
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @JsonDeserialize
    public static final class TempBiome {
        @JsonProperty
        @JsonAlias({"dict"})
        private BiomeDictionary dictionary;

        @JsonProperty
        private Decorator[] decorators = Decorator.EMPTY_ARRAY;
        @JsonProperty
        private Populator[] populators = Populator.EMPTY_ARRAY;

        private BiomeElevation elevation = BiomeElevation.DEFAULT;

        @JsonProperty
        private double temperature = 0.5d;
        @JsonProperty
        private double rainfall    = 0.5d;

        public GenerationBiome build(@NonNull Identifier id, int internalId) {
            Objects.requireNonNull(this.dictionary, "dictionary must be set!");

            return new GenerationBiome(this, id, internalId);
        }

        @JsonSetter("dictionary")
        private void setDictionary(Identifier dictionaryId) {
            this.dictionary = StandardGeneratorStores.biomeDictionary().find(dictionaryId);
        }

        @JsonSetter("elevationAbsolute")
        private void setElevationAbsolute(BiomeElevation.Absolute elevationAbsolute) {
            this.elevation = Objects.requireNonNull(elevationAbsolute, "elevationAbsolute");
        }

        @JsonSetter("elevationVanilla")
        private void setElevationVanilla(BiomeElevation.Vanilla elevationVanilla) {
            this.elevation = Objects.requireNonNull(elevationVanilla, "elevationVanilla");
        }
    }
}
