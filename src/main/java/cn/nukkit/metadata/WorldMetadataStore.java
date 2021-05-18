package cn.nukkit.metadata;

import cn.nukkit.world.World;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class WorldMetadataStore extends MetadataStore {

    @Override
    protected String disambiguate(Metadatable world, String metadataKey) {
        if (!(world instanceof World)) {
            throw new IllegalArgumentException("Argument must be a Level instance");
        }
        return (((World) world).getName() + ":" + metadataKey).toLowerCase();
    }
}
