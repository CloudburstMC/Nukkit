package cn.nukkit.metadata;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.PluginException;
import cn.nukkit.utils.ServerException;

import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class MetadataStore {

    private Map<String, Map<Plugin, MetadataValue>> metadataMap = new HashMap<>();

    public void setMetadata(Object subject, String metadataKey, MetadataValue newMetadataValue) throws Exception {
        if (newMetadataValue == null) {
            throw new ServerException("Value cannot be null");
        }
        Plugin owningPlugin = newMetadataValue.getOwningPlugin();
        if (owningPlugin == null) {
            throw new PluginException("Plugin cannot be null");
        }
        String key = this.disambiguate((Metadatable) subject, metadataKey);
        Map<Plugin, MetadataValue> entry = this.metadataMap.get(key);
        if (entry == null) {
            entry = new WeakHashMap<>(1);
            this.metadataMap.put(key, entry);
        }
        entry.put(owningPlugin, newMetadataValue);
    }

    public List<MetadataValue> getMetadata(Object subject, String metadataKey) throws Exception {
        String key = this.disambiguate((Metadatable) subject, metadataKey);
        if (this.metadataMap.containsKey(key)) {
            Collection values = ((Map) this.metadataMap.get(key)).values();
            return Collections.unmodifiableList(new ArrayList<>(values));
        }
        return Collections.emptyList();
    }

    public boolean hasMetadata(Object subject, String metadataKey) throws Exception {
        return this.metadataMap.containsKey(this.disambiguate((Metadatable) subject, metadataKey));
    }

    public void removeMetadata(Object subject, String metadataKey, Plugin owningPlugin) throws Exception {
        if (owningPlugin == null) {
            throw new PluginException("Plugin cannot be null");
        }
        String key = this.disambiguate((Metadatable) subject, metadataKey);
        Map entry = (Map) this.metadataMap.get(key);
        if (entry == null) {
            return;
        }
        entry.remove(owningPlugin);
        if (entry.isEmpty()) {
            this.metadataMap.remove(key);
        }
    }

    public void invalidateAll(Plugin owningPlugin) throws Exception {
        if (owningPlugin == null) {
            throw new PluginException("Plugin cannot be null");
        }
        this.metadataMap.values().stream().filter(value -> value.containsKey(owningPlugin)).forEach(value -> {
            ((MetadataValue) value.get(owningPlugin)).invalidate();
        });
    }

    protected abstract String disambiguate(Metadatable subject, String metadataKey) throws Exception;
}
