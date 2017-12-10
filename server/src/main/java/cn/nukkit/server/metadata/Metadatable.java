package cn.nukkit.server.metadata;

import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface Metadatable {

    void setMetadata(String metadataKey, MetadataValue newMetadataValue) throws Exception;

    List<MetadataValue> getMetadata(String metadataKey) throws Exception;

    boolean hasMetadata(String metadataKey) throws Exception;

    void removeMetadata(String metadataKey, Plugin owningPlugin) throws Exception;
}
