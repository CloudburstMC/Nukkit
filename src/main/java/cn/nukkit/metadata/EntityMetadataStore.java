package cn.nukkit.metadata;

import cn.nukkit.entity.Entity;

import java.io.IOException;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityMetadataStore extends MetadataStore {

    @Override
    protected String disambiguate(Metadatable entity, String metadataKey) throws Exception {
        if (!(entity instanceof Entity)) {
                throw new IllegalArgumentException("Argument must be an Entity instance");
        }
        return ((Entity) entity).getId() + ":" + metadataKey;
    }
}
