package cn.nukkit.level.generator.standard.store;

import cn.nukkit.Nukkit;
import cn.nukkit.level.generator.standard.StandardGeneratorUtils;
import cn.nukkit.level.generator.standard.biome.BiomeDictionary;
import cn.nukkit.level.generator.standard.biome.GenerationBiome;
import cn.nukkit.level.generator.standard.gen.decorator.Decorator;
import cn.nukkit.level.generator.standard.gen.replacer.BlockReplacer;
import cn.nukkit.level.generator.standard.misc.IntRange;
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
        private static final IntRange DEFAULT_HEIGHT_RANGE = new IntRange(65, 75);

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
        @JsonAlias({"heightRange"})
        private IntRange height = DEFAULT_HEIGHT_RANGE;

        public GenerationBiome build(@NonNull Identifier id) {
            return new GenerationBiome(this, id);
        }

        @JsonSetter("dictionary")
        private void setDictionary(Identifier dictionaryId) {
            this.dictionary = StandardGeneratorStores.biomeDictionary().find(dictionaryId);
        }
    }
}
