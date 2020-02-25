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

    public BlockReplacerRegistry blockReplacerRegistry()    {
        return BLOCK_REPLACER_REGISTRY_CACHE.get();
    }
}
