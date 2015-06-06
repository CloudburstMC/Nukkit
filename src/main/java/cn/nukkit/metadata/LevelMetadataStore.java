package cn.nukkit.metadata;

import cn.nukkit.level.Level;
import com.sun.javaws.exceptions.InvalidArgumentException;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LevelMetadataStore extends MetadataStore {

    @Override
    protected String disambiguate(Metadatable level, String metadataKey) throws Exception {
        if (!(level instanceof Level)) {
            throw new InvalidArgumentException(new String[]{"Argument must be a Level instance"});
        }
        return (((Level) level).getName() + ":" + metadataKey).toLowerCase();
    }
}
