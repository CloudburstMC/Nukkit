package cn.nukkit.metadata;

import cn.nukkit.IPlayer;
import com.sun.javaws.exceptions.InvalidArgumentException;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PlayerMetadataStore extends MetadataStore {

    @Override
    protected String disambiguate(Metadatable player, String metadataKey) throws Exception {
        if (!(player instanceof IPlayer)) {
            throw new InvalidArgumentException(new String[]{"Argument must be an IPlayer instance"});
        }
        return (((IPlayer) player).getName() + ":" + metadataKey).toLowerCase();
    }
}
