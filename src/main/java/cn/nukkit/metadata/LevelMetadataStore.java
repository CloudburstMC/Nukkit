package cn.nukkit.metadata;

import cn.nukkit.level.Level;

import java.util.Locale;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class LevelMetadataStore extends MetadataStore {

    @Override
    protected String disambiguate(Metadatable level, String metadataKey) {
        if (!(level instanceof Level)) {
            throw new IllegalArgumentException("Argument must be a Level instance");
        }
        return (((Level) level).getName() + ':' + metadataKey).toLowerCase(Locale.ROOT);
    }
}
