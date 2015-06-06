package cn.nukkit.level;

import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.plugin.Plugin;

import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Level implements Metadatable {

    public String getName() {
        return "TODO！！！！！！！！";
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        //todo
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        //todo
        return null;
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        //todo
        return false;
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        //todo
    }
}
