package cn.nukkit.item;

import it.unimi.dsi.fastutil.ints.Int2IntMap;

public class RuntimeItemMapping {

    private final Int2IntMap legacyNetworkMap;
    private final Int2IntMap networkLegacyMap;
    private final byte[] itemDataPalette;

    public RuntimeItemMapping(byte[] itemDataPalette, Int2IntMap legacyNetworkMap, Int2IntMap networkLegacyMap) {
        this.itemDataPalette = itemDataPalette;
        this.legacyNetworkMap = legacyNetworkMap;
        this.networkLegacyMap = networkLegacyMap;
        this.legacyNetworkMap.defaultReturnValue(-1);
        this.networkLegacyMap.defaultReturnValue(-1);
    }

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

    public int getLegacyFullId(int networkId) {
        int fullId = networkLegacyMap.get(networkId);
        if (fullId == -1) {
            throw new IllegalArgumentException("Unknown network mapping: " + networkId);
        }
        return fullId;
    }

    public byte[] getItemDataPalette() {
        return this.itemDataPalette;
    }
}
