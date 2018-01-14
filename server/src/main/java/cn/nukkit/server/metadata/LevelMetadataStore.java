package cn.nukkit.server.metadata;

import cn.nukkit.server.level.NukkitLevel;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LevelMetadataStore extends MetadataStore {

    @Override
    protected String disambiguate(Metadatable level, String metadataKey) {
        if (!(level instanceof NukkitLevel)) {
            throw new IllegalArgumentException("Argument must be a NukkitLevel instance");
        }
        return (((NukkitLevel) level).getName() + ":" + metadataKey).toLowerCase();
    }
}
