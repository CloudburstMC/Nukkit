package cn.nukkit.level.generator.standard.registry;

import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.cache.Cache;

/**
 * Registries for looking up the various different resources required for parsing the config for the NukkitX standard generator.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class StandardGeneratorRegistries {
    private final Cache<BlockReplacerRegistry> BLOCK_REPLACER_REGISTRY_CACHE = Cache.late(BlockReplacerRegistry::new);
    private final Cache<NoiseSourceRegistry> NOISE_SOURCE_REGISTRY_CACHE = Cache.late(NoiseSourceRegistry::new);

    public BlockReplacerRegistry blockReplacerRegistry()    {
        return BLOCK_REPLACER_REGISTRY_CACHE.get();
    }

    public NoiseSourceRegistry noiseSourceRegistry()    {
        return NOISE_SOURCE_REGISTRY_CACHE.get();
    }
}
