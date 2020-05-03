package cn.nukkit.level.generator.standard.biome;

import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.generator.standard.finish.Finisher;
import cn.nukkit.level.generator.standard.generation.decorator.Decorator;
import cn.nukkit.level.generator.standard.misc.NextGenerationPass;
import cn.nukkit.level.generator.standard.population.Populator;
import cn.nukkit.level.generator.standard.store.GenerationBiomeStore;
import cn.nukkit.registry.BiomeRegistry;
import cn.nukkit.utils.Identifier;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PorkUtil;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Representation of a biome used during terrain generation.
 *
 * @author DaPorkchop_
 */
@JsonDeserialize(using = GenerationBiomeDeserializer.class)
public final class GenerationBiome {
    private final Identifier id;
    private final Biome biome;

    private final Decorator[] decorators;
    private final Populator[] populators;
    private final Finisher[] finishers;

    private final BiomeElevation elevation;

    private final int runtimeId;
    private final int internalId;

    public GenerationBiome(@NonNull GenerationBiomeStore.TempBiome temp, @NonNull Identifier id, int internalId) {
        Biome biome = BiomeRegistry.get().getBiome(PorkUtil.fallbackIfNull(temp.getRealId(), id));

        Decorator[] decorators = temp.getDecorators();
        Populator[] populators = temp.getPopulators();
        Finisher[] finishers = temp.getFinishers();

        BiomeElevation elevation = temp.getElevation();

        GenerationBiome parent = temp.getParent();
        if (parent != null) {
            if (biome == null) {
                biome = parent.biome;
            }

            if (decorators == null) {
                decorators = Decorator.EMPTY_ARRAY;
            } else if (decorators.length == 0) {
                decorators = parent.decorators;
            } else {
                decorators = Arrays.stream(decorators)
                        .flatMap(decorator -> decorator instanceof NextGenerationPass ? Arrays.stream(parent.decorators) : Stream.of(decorator))
                        .toArray(Decorator[]::new);
            }
            if (populators == null) {
                populators = Populator.EMPTY_ARRAY;
            } else if (populators.length == 0) {
                populators = parent.populators;
            } else {
                populators = Arrays.stream(populators)
                        .flatMap(populator -> populator instanceof NextGenerationPass ? Arrays.stream(parent.populators) : Stream.of(populator))
                        .toArray(Populator[]::new);
            }
            if (finishers == null) {
                finishers = Finisher.EMPTY_ARRAY;
            } else if (finishers.length == 0) {
                finishers = parent.finishers;
            } else {
                finishers = Arrays.stream(finishers)
                        .flatMap(finisher -> finisher instanceof NextGenerationPass ? Arrays.stream(parent.finishers) : Stream.of(finisher))
                        .toArray(Finisher[]::new);
            }

            if (elevation == BiomeElevation.DEFAULT) {
                elevation = parent.elevation;
            }
        } else {
            if (decorators == null) {
                decorators = Decorator.EMPTY_ARRAY;
            }
            if (populators == null) {
                populators = Populator.EMPTY_ARRAY;
            }
            if (finishers == null) {
                finishers = Finisher.EMPTY_ARRAY;
            }
        }

        this.id = id;
        Preconditions.checkState((this.biome = biome) != null, temp.getRealId() == null ? "Unknown biome %s! Consider adding a 'realId' entry if this is a virtual biome." : "Unknown real biome %s!", PorkUtil.fallbackIfNull(temp.getRealId(), id));

        this.decorators = decorators;
        this.populators = populators;
        this.finishers = finishers;

        this.elevation = elevation;

        this.runtimeId = BiomeRegistry.get().getRuntimeId(this.biome);
        this.internalId = internalId;
    }

    public Identifier getId() {
        return this.id;
    }

    public Biome getBiome() {
        return this.biome;
    }

    public Decorator[] getDecorators() {
        return this.decorators;
    }

    public Populator[] getPopulators() {
        return this.populators;
    }

    public Finisher[] getFinishers() {
        return this.finishers;
    }

    public BiomeElevation getElevation() {
        return this.elevation;
    }

    public int getRuntimeId() {
        return this.runtimeId;
    }

    public int getInternalId() {
        return this.internalId;
    }
}
