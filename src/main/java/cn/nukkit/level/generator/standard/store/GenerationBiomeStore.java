package cn.nukkit.level.generator.standard.store;

import cn.nukkit.Nukkit;
import cn.nukkit.level.generator.standard.StandardGeneratorUtils;
import cn.nukkit.level.generator.standard.biome.BiomeElevation;
import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.finish.Finisher;
import cn.nukkit.level.generator.standard.generation.decorator.Decorator;
import cn.nukkit.level.generator.standard.population.Populator;
import cn.nukkit.utils.Identifier;
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
        private Identifier realId;

        @JsonProperty
        private GenerationBiome parent;

        @JsonProperty
        private Decorator[] decorators = Decorator.EMPTY_ARRAY;
        @JsonProperty
        private Populator[] populators = Populator.EMPTY_ARRAY;
        @JsonProperty
        private Finisher[] finishers = Finisher.EMPTY_ARRAY;

        private BiomeElevation elevation = BiomeElevation.DEFAULT;

        public GenerationBiome build(@NonNull Identifier id, int internalId) {
            return new GenerationBiome(this, id, internalId);
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
