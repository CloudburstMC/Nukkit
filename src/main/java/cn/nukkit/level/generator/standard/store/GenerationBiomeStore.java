package cn.nukkit.level.generator.standard.store;

import cn.nukkit.Nukkit;
import cn.nukkit.level.generator.standard.StandardGeneratorUtils;
import cn.nukkit.level.generator.standard.biome.BiomeDictionary;
import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.gen.decorator.Decorator;
import cn.nukkit.level.generator.standard.gen.replacer.BlockReplacer;
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

/**
 * Store for {@link GenerationBiome}.
 *
 * @author DaPorkchop_
 * @see StandardGeneratorStores#generationBiome()
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class GenerationBiomeStore extends AbstractGeneratorStore<GenerationBiome> {
    @Override
    protected GenerationBiome compute(@NonNull Identifier id) throws IOException {
        try (InputStream in = StandardGeneratorUtils.read("biome", id)) {
            return Nukkit.YAML_MAPPER.readValue(in, TempBiome.class).build(id);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @JsonDeserialize
    public static final class TempBiome {
        @JsonProperty(required = true)
        @JsonAlias({"dict"})
        private BiomeDictionary dictionary;

        @JsonProperty
        private BlockReplacer[] replacers  = BlockReplacer.EMPTY_ARRAY;
        @JsonProperty
        private Decorator[]     decorators = Decorator.EMPTY_ARRAY;
        @JsonProperty
        private Populator[]     populators = Populator.EMPTY_ARRAY;

        @JsonProperty
        private BiomeHeight height = BiomeHeight.DEFAULT_HEIGHT_RANGE;

        public GenerationBiome build(@NonNull Identifier id) {
            return new GenerationBiome(this, id);
        }

        @JsonSetter("dictionary")
        private void setDictionary(Identifier dictionaryId) {
            this.dictionary = StandardGeneratorStores.biomeDictionary().find(dictionaryId);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @JsonDeserialize
    public static final class BiomeHeight {
        private static final BiomeHeight DEFAULT_HEIGHT_RANGE = new BiomeHeight();

        @JsonProperty
        @JsonAlias({"base"})
        private double baseHeight      = 0.1d;
        @JsonProperty
        @JsonAlias({"variation"})
        private double heightVariation = 0.2d;

        public double correctedBaseHeight() {
            return this.baseHeight;
            //return this.baseHeight * 17.0d / 64.0d - 1.0d / 256.0d;
        }

        public double correctedHeightVariation() {
            return this.heightVariation;
            //return 2.4d * this.heightVariation + 4.0d / 15.0d;
        }
    }
}
