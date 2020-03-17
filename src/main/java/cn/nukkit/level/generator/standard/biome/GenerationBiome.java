package cn.nukkit.level.generator.standard.biome;

import cn.nukkit.level.generator.standard.gen.decorator.Decorator;
import cn.nukkit.level.generator.standard.misc.NextGenerationPass;
import cn.nukkit.level.generator.standard.pop.Populator;
import cn.nukkit.level.generator.standard.store.GenerationBiomeStore;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.NonNull;

import java.util.Arrays;
import java.util.stream.Stream;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Representation of a biome used during terrain generation.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize(using = GenerationBiomeDeserializer.class)
public final class GenerationBiome {
    private final Identifier      id;
    private final BiomeDictionary dictionary;

    private final Decorator[] decorators;
    private final Populator[] populators;

    private final BiomeElevation elevation;

    private final double temperature;
    private final double rainfall;

    private final int runtimeId;
    private final int internalId;

    public GenerationBiome(@NonNull GenerationBiomeStore.TempBiome temp, @NonNull Identifier id, int internalId) {
        this.id = id;
        this.dictionary = temp.getDictionary();

        Decorator[] decorators = fallbackIfNull(temp.getDecorators(), Decorator.EMPTY_ARRAY);
        Populator[] populators = fallbackIfNull(temp.getPopulators(), Populator.EMPTY_ARRAY);

        BiomeElevation elevation = temp.getElevation();

        double temperature = temp.getTemperature();
        double rainfall = temp.getRainfall();

        GenerationBiome parent = temp.getParent();
        if (parent != null) {
            if (decorators == Decorator.EMPTY_ARRAY) {
                decorators = parent.decorators;
            } else {
                decorators = Arrays.stream(decorators)
                        .flatMap(decorator -> decorator instanceof NextGenerationPass ? Arrays.stream(parent.getDecorators()) : Stream.of(decorator))
                        .toArray(Decorator[]::new);
            }
            if (populators == Populator.EMPTY_ARRAY) {
                populators = parent.populators;
            } else {
                populators = Arrays.stream(decorators)
                        .flatMap(populator -> populator instanceof NextGenerationPass ? Arrays.stream(parent.getPopulators()) : Stream.of(populator))
                        .toArray(Populator[]::new);
            }

            if (elevation == BiomeElevation.DEFAULT) {
                elevation = parent.elevation;
            }

            if (Double.isNaN(temperature)) {
                temperature = parent.temperature;
            }
            if (Double.isNaN(rainfall)) {
                temperature = parent.rainfall;
            }
        } else {
            if (Double.isNaN(temperature)) {
                temperature = 0.5d;
            }
            if (Double.isNaN(rainfall)) {
                temperature = 0.5d;
            }
        }

        this.decorators = decorators;
        this.populators = populators;
        this.elevation = elevation;
        this.temperature = temperature;
        this.rainfall = rainfall;

        this.runtimeId = this.dictionary.get(id);
        this.internalId = internalId;
    }

    public Identifier getId() {
        return this.id;
    }

    public BiomeDictionary getDictionary() {
        return this.dictionary;
    }

    public Decorator[] getDecorators() {
        return this.decorators;
    }

    public Populator[] getPopulators() {
        return this.populators;
    }

    public BiomeElevation getElevation() {
        return this.elevation;
    }

    public double getTemperature() {
        return this.temperature;
    }

    public double getRainfall() {
        return this.rainfall;
    }

    public int getRuntimeId() {
        return this.runtimeId;
    }

    public int getInternalId() {
        return this.internalId;
    }

}
