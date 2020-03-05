package cn.nukkit.level.generator.standard.biome;

import cn.nukkit.level.generator.standard.gen.decorator.Decorator;
import cn.nukkit.level.generator.standard.gen.replacer.BlockReplacer;
import cn.nukkit.level.generator.standard.pop.Populator;
import cn.nukkit.level.generator.standard.store.GenerationBiomeStore;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.NonNull;

/**
 * Representation of a biome used during terrain generation.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize(using = GenerationBiomeDeserializer.class)
public final class GenerationBiome {
    private final Identifier      id;
    private final BiomeDictionary dictionary;

    private final BlockReplacer[] replacers;
    private final Decorator[]     decorators;
    private final Populator[]     populators;

    private final double baseHeight;
    private final double heightVariation;

    private final double temperature;
    private final double rainfall;

    private final int runtimeId;

    public GenerationBiome(@NonNull GenerationBiomeStore.TempBiome temp, @NonNull Identifier id) {
        this.id = id;
        this.dictionary = temp.getDictionary();
        this.replacers = temp.getReplacers();
        this.decorators = temp.getDecorators();
        this.populators = temp.getPopulators();

        this.baseHeight = temp.getBaseHeight();
        this.heightVariation = temp.getHeightVariation();

        this.temperature = temp.getTemperature();
        this.rainfall = temp.getRainfall();

        this.runtimeId = this.dictionary.get(id);
    }

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

    public double getBaseHeight() {
        return this.baseHeight;
    }

    public double getHeightVariation() {
        return this.heightVariation;
    }

    public int getRuntimeId() {
        return this.runtimeId;
    }
}
