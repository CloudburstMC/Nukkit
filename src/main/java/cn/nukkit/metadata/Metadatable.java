package cn.nukkit.metadata;

import cn.nukkit.plugin.Plugin;

import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface Metadatable {

    void setMetadata(String metadataKey, MetadataValue newMetadataValue);

    List<MetadataValue> getMetadata(String metadataKey);

    boolean hasMetadata(String metadataKey);

    void removeMetadata(String metadataKey, Plugin owningPlugin);
}
