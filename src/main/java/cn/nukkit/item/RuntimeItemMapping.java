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

/**
 * Responsible for mapping item full ids, item network ids and item namespaced ids between each other.
 * <ul>
 * <li>A <b>full id</b> is a combination of <b>item id</b> and <b>item damage</b>. 
 * The way they are combined may change in future, so you should not combine them by yourself and neither store them
 * permanently. It's mainly used to preserve backward compatibility with plugins that don't support <em>namespaced ids</em>.
 * <li>A <b>network id</b> is an id that is used to communicated with the client, it may change between executions of the
 * same server version depending on how the plugins are setup.
 * <li>A <b>namespaced id</b> is the new way Mojang saves the ids, a string like <code>minecraft:stone</code>. It may change
 * in Minecraft updates but tends to be permanent, unless Mojang decides to change them for some random reasons...
 */
@Since("1.4.0.0-PN")
public class RuntimeItemMapping {

    private final Int2IntMap legacyNetworkMap;
    private final Int2IntMap networkLegacyMap;
    private final byte[] itemDataPalette;

    private final Map<String, OptionalInt> namespaceNetworkMap;
    private final Int2ObjectMap<String> networkNamespaceMap;

    @Since("1.4.0.0-PN")
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
    @Since("1.4.0.0-PN")
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

    /**
     * Returns the <b>network id</b> based on the <b>full id</b> of the given item.
     * @param item Given item
     * @return The <b>network id</b>
     * @throws IllegalArgumentException If the mapping of the <b>full id</b> to the <b>network id</b> is unknown
     */
    @Since("1.4.0.0-PN")
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

    /**
     * Returns the <b>full id</b> of a given <b>network id</b>.
     * @param networkId The given <b>network id</b>
     * @return The <b>full id</b>
     * @throws IllegalArgumentException If the mapping of the <b>full id</b> to the <b>network id</b> is unknown
     */
    @Since("1.4.0.0-PN")
    public int getLegacyFullId(int networkId) {
        int fullId = networkLegacyMap.get(networkId);
        if (fullId == -1) {
            throw new IllegalArgumentException("Unknown network mapping: " + networkId);
        }
        return fullId;
    }

    @Since("1.4.0.0-PN")
    public byte[] getItemDataPalette() {
        return this.itemDataPalette;
    }

    /**
     * Returns the <b>namespaced id</b> of a given <b>network id</b>.
     * @param networkId The given <b>network id</b>
     * @return The <b>namespace id</b> or {@code null} if it is unknown
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public String getNamespacedIdByNetworkId(int networkId) {
        return networkNamespaceMap.get(networkId);
    }

    /**
     * Returns the <b>network id</b> of a given <b>namespaced id</b>.
     * @param namespaceId The given <b>namespaced id</b>
     * @return A <b>network id</b> wrapped in {@link OptionalInt} or an empty {@link OptionalInt} if it is unknown
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public OptionalInt getNetworkIdByNamespaceId(@Nonnull String namespaceId) {
        return namespaceNetworkMap.getOrDefault(namespaceId, OptionalInt.empty());
    }

    /**
     * Creates a new instance of the respective {@link Item} by the <b>namespaced id</b>.
     * @param namespaceId The namespaced id
     * @param amount How many items will be in the stack.
     * @return The correct {@link Item} instance with the write <b>item id</b> and <b>item damage</b> values.
     * @throws IllegalArgumentException If there are unknown mappings in the process. 
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Item getItemByNamespaceId(@Nonnull String namespaceId, int amount) {
        int legacyFullId = getLegacyFullId(
                getNetworkIdByNamespaceId(namespaceId)
                        .orElseThrow(()-> new IllegalArgumentException("The network id of \""+namespaceId+"\" is unknown"))
        );
        if (RuntimeItems.hasData(legacyFullId)) {
            return Item.get(RuntimeItems.getId(legacyFullId), RuntimeItems.getData(legacyFullId), amount);
        } else {
            Item item = Item.get(RuntimeItems.getId(legacyFullId));
            item.setCount(amount);
            return item;
        }
    }
}
