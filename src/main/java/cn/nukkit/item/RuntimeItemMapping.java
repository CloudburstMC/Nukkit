package cn.nukkit.item;

import cn.nukkit.api.API;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;

@Since("1.3.2.0-PN")
public class RuntimeItemMapping {

    private final Int2IntMap legacyNetworkMap;
    private final Int2IntMap networkLegacyMap;
    private final byte[] itemDataPalette;

    private final Map<String, OptionalInt> namespaceNetworkMap;
    private final Int2ObjectMap<String> networkNamespaceMap;

    @Since("1.3.2.0-PN")
    public RuntimeItemMapping(byte[] itemDataPalette, Int2IntMap legacyNetworkMap, Int2IntMap networkLegacyMap) {
        this.itemDataPalette = itemDataPalette;
        this.legacyNetworkMap = legacyNetworkMap;
        this.networkLegacyMap = networkLegacyMap;
        this.legacyNetworkMap.defaultReturnValue(-1);
        this.networkLegacyMap.defaultReturnValue(-1);
        this.namespaceNetworkMap = new LinkedHashMap<>();
        this.networkNamespaceMap = new Int2ObjectOpenHashMap<>();
    }

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    @API(definition = API.Definition.INTERNAL, usage = API.Usage.BLEEDING)
    public RuntimeItemMapping(
            byte[] itemDataPalette, Int2IntMap legacyNetworkMap, Int2IntMap networkLegacyMap,
            Map<String, Integer> namespaceNetworkMap, Int2ObjectMap<String> networkNamespaceMap) {
        this.itemDataPalette = itemDataPalette;
        this.legacyNetworkMap = legacyNetworkMap;
        this.networkLegacyMap = networkLegacyMap;
        this.legacyNetworkMap.defaultReturnValue(-1);
        this.networkLegacyMap.defaultReturnValue(-1);
        this.networkNamespaceMap = networkNamespaceMap;
        this.namespaceNetworkMap = namespaceNetworkMap.entrySet().stream()
                .map(e-> new AbstractMap.SimpleEntry<>(e.getKey(), OptionalInt.of(e.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Since("1.3.2.0-PN")
    public int getNetworkFullId(Item item) {
        int fullId = RuntimeItems.getFullId(item.getId(), item.hasMeta() ? item.getDamage() : -1);
        int networkId = this.legacyNetworkMap.get(fullId);
        if (networkId == -1) {
            networkId = this.legacyNetworkMap.get(RuntimeItems.getFullId(item.getId(), 0));
        }
        if (networkId == -1) {
            throw new IllegalArgumentException("Unknown item mapping " + item);
        }

        return networkId;
    }

    @Since("1.3.2.0-PN")
    public int getLegacyFullId(int networkId) {
        int fullId = networkLegacyMap.get(networkId);
        if (fullId == -1) {
            throw new IllegalArgumentException("Unknown network mapping: " + networkId);
        }
        return fullId;
    }

    @Since("1.3.2.0-PN")
    public byte[] getItemDataPalette() {
        return this.itemDataPalette;
    }

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    @Nullable
    public String getNamespacedIdByNetworkId(int networkId) {
        return networkNamespaceMap.get(networkId);
    }

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    @Nonnull
    public OptionalInt getNetworkIdByNamespaceId(@Nonnull String namespaceId) {
        return namespaceNetworkMap.getOrDefault(namespaceId, OptionalInt.empty());
    }

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    @Nullable
    public Item getItemByNamespaceId(@Nonnull String namespaceId, int amount) {
        OptionalInt netId = getNetworkIdByNamespaceId(namespaceId);
        if (!netId.isPresent()) {
            return null;
        }
        int legacyFullId = getLegacyFullId(netId.getAsInt());
        if (RuntimeItems.hasData(legacyFullId)) {
            return Item.get(RuntimeItems.getId(legacyFullId), RuntimeItems.getData(legacyFullId), amount);
        } else {
            Item item = Item.get(RuntimeItems.getId(legacyFullId));
            item.setCount(amount);
            return item;
        }
    }
}
