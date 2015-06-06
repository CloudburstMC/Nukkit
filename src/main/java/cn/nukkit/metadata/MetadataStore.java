package cn.nukkit.metadata;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.PluginException;
import cn.nukkit.utils.ServerException;

import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class MetadataStore<T> {

    private HashMap<String, Map<Plugin, MetadataValue>> metadataMap = new HashMap<String, Map<Plugin, MetadataValue>>();

    public void setMetadata(T subject, String metadataKey, MetadataValue newMetadataValue) {
        if (newMetadataValue == null) {
            throw new ServerException("Value cannot be null");
        }
        Plugin owningPlugin = newMetadataValue.getOwningPlugin();
        if (owningPlugin == null) {
            throw new PluginException("Plugin cannot be null");
        }
        String key = this.disambiguate(subject, metadataKey);
        Map<Plugin, MetadataValue> entry = this.metadataMap.get(key);
        if (entry == null) {
            entry = new WeakHashMap<Plugin, MetadataValue>(1);
            this.metadataMap.put(key, entry);
        }
        entry.put(owningPlugin, newMetadataValue);
    }

    public List<MetadataValue> getMetadata(T subject, String metadataKey) {
        String key = this.disambiguate(subject, metadataKey);
        if (this.metadataMap.containsKey(key)) {
            Collection values = ((Map) this.metadataMap.get(key)).values();
            return Collections.unmodifiableList(new ArrayList<MetadataValue>(values));
        }
        return Collections.emptyList();
    }

    public boolean hasMetadata(T subject, String metadataKey) {
        return this.metadataMap.containsKey(this.disambiguate(subject, metadataKey));
    }

    public void removeMetadata(T subject, String metadataKey, Plugin owningPlugin) {
        if (owningPlugin == null) {
            throw new PluginException("Plugin cannot be null");
        }
        String key = this.disambiguate(subject, metadataKey);
        Map entry = (Map) this.metadataMap.get(key);
        if (entry == null) {
            return;
        }
        entry.remove(owningPlugin);
        if (entry.isEmpty()) {
            this.metadataMap.remove(key);
        }
    }

    public void invalidateAll(Plugin owningPlugin) {
        if (owningPlugin == null) {
            throw new PluginException("Plugin cannot be null");
        }
        for (Map value : this.metadataMap.values()) {
            if (value.containsKey(owningPlugin)) {
                ((MetadataValue) value.get(owningPlugin)).invalidate();
            }
        }
    }

    protected abstract String disambiguate(T subject, String metadataKey);
}
