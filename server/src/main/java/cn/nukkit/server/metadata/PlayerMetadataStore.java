package cn.nukkit.server.metadata;

import cn.nukkit.server.IPlayer;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PlayerMetadataStore extends MetadataStore {

    @Override
    protected String disambiguate(Metadatable player, String metadataKey) {
        if (!(player instanceof IPlayer)) {
            throw new IllegalArgumentException("Argument must be an IPlayer instance");
        }
        return (((IPlayer) player).getName() + ":" + metadataKey).toLowerCase();
    }
}
