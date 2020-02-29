package cn.nukkit.level.generator.standard.biome;

import cn.nukkit.level.generator.standard.gen.decorator.Decorator;
import cn.nukkit.level.generator.standard.gen.replacer.BlockReplacer;
import cn.nukkit.level.generator.standard.pop.Populator;
import cn.nukkit.level.generator.standard.store.StandardGeneratorStores;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Representation of a biome used during terrain generation.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize
public final class GenerationBiome {
    @JsonProperty(required = true)
    private Identifier      id;
    @JsonProperty(required = true)
    @JsonAlias({"dict"})
    private BiomeDictionary dictionary;

    @JsonProperty
    private BlockReplacer[] replacers  = BlockReplacer.EMPTY_ARRAY;
    @JsonProperty
    private Decorator[]     decorators = Decorator.EMPTY_ARRAY;
    @JsonProperty
    private Populator[]     populators = Populator.EMPTY_ARRAY;

    private int runtimeId = -1;

    public Identifier getId() {
        return this.id;
    }

    public BiomeDictionary getDictionary() {
        return this.dictionary;
    }

    public BlockReplacer[] getReplacers() {
        return this.replacers;
    }

    public Decorator[] getDecorators() {
        return this.decorators;
    }

    public Populator[] getPopulators() {
        return this.populators;
    }

    public int getRuntimeId() {
        return this.runtimeId;
    }

    @JsonSetter("id")
    private void setId(Identifier id) {
        this.id = id;
        if (this.dictionary != null) {
            this.runtimeId = this.dictionary.get(id);
        }
    }

    @JsonSetter("dictionary")
    private void setDictionary(Identifier dictionaryId) {
        BiomeDictionary dictionary = this.dictionary = StandardGeneratorStores.biomeDictionary().find(dictionaryId);
        if (this.id != null) {
            this.runtimeId = dictionary.get(this.id);
        }
    }
}
