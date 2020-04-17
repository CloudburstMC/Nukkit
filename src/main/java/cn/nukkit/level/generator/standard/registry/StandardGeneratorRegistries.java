package cn.nukkit.level.generator.standard.registry;

import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.ref.Ref;

/**
 * Registries for looking up the various different resources required for parsing the config for the NukkitX standard generator.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class StandardGeneratorRegistries {
    private final Ref<BiomeFilterRegistry> BIOME_FILTER_REGISTRY_CACHE = Ref.late(BiomeFilterRegistry::new);
    private final Ref<BiomeMapRegistry> BIOME_MAP_REGISTRY_CACHE = Ref.late(BiomeMapRegistry::new);
    private final Ref<DecoratorRegistry> DECORATOR_REGISTRY_CACHE = Ref.late(DecoratorRegistry::new);
    private final Ref<DensitySourceRegistry> DENSITY_SOURCE_REGISTRY_CACHE = Ref.late(DensitySourceRegistry::new);
    private final Ref<FinisherRegistry> FINISHER_REGISTRY_CACHE = Ref.late(FinisherRegistry::new);
    private final Ref<NoiseGeneratorRegistry> NOISE_GENERATOR_REGISTRY_CACHE = Ref.late(NoiseGeneratorRegistry::new);
    private final Ref<PopulatorRegistry> POPULATOR_REGISTRY_CACHE = Ref.late(PopulatorRegistry::new);

    public BiomeFilterRegistry biomeFilter() {
        return BIOME_FILTER_REGISTRY_CACHE.get();
    }

    public BiomeMapRegistry biomeMap() {
        return BIOME_MAP_REGISTRY_CACHE.get();
    }

    public DecoratorRegistry decorator() {
        return DECORATOR_REGISTRY_CACHE.get();
    }

    public DensitySourceRegistry densitySource() {
        return DENSITY_SOURCE_REGISTRY_CACHE.get();
    }

    public FinisherRegistry finisher() {
        return FINISHER_REGISTRY_CACHE.get();
    }

    public NoiseGeneratorRegistry noiseGenerator() {
        return NOISE_GENERATOR_REGISTRY_CACHE.get();
    }

    public PopulatorRegistry populator() {
        return POPULATOR_REGISTRY_CACHE.get();
    }
}
