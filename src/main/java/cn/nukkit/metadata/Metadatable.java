package cn.nukkit.metadata;

import cn.nukkit.plugin.Plugin;

import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract interface Metadatable {

    public abstract void setMetadata(String metadataKey, MetadataValue newMetadataValue);

    public abstract List<MetadataValue> getMetadata(String metadataKey);

    public abstract boolean hasMetadata(String metadataKey);

    public abstract void removeMetadata(String metadataKey, Plugin owningPlugin);
}
