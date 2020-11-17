package cn.nukkit.item;

import cn.nukkit.Server;
import cn.nukkit.utils.BinaryStream;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class RuntimeItems {

    private static final Gson GSON = new Gson();
    private static final Type ENTRY_TYPE = new TypeToken<ArrayList<Entry>>(){}.getType();

    public static final byte[] ITEM_DATA_PALETTE;

    private static final Int2IntMap LEGACY_NETWORK_MAP = new Int2IntOpenHashMap();
    private static final Int2IntMap NETWORK_LEGACY_MAP = new Int2IntOpenHashMap();

    static {
        List<Entry> entries;
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_item_ids.json");
             InputStreamReader reader = new InputStreamReader(stream)) {
            entries = GSON.fromJson(reader, ENTRY_TYPE);
        } catch (NullPointerException | IOException e) {
            throw new AssertionError("Unable to load runtime_item_ids.json", e);
        }

        BinaryStream paletteBuffer = new BinaryStream();
        paletteBuffer.putUnsignedVarInt(entries.size());

        LEGACY_NETWORK_MAP.defaultReturnValue(-1);
        NETWORK_LEGACY_MAP.defaultReturnValue(-1);

        for (Entry entry : entries) {

            paletteBuffer.putString(entry.name);
            paletteBuffer.putLShort(entry.id);
            paletteBuffer.putBoolean(false); // Component item

            if (entry.oldId != null) {
                boolean hasData = entry.oldData != null;
                int fullId = getFullId(entry.oldId, hasData ? entry.oldData : 0);
                LEGACY_NETWORK_MAP.put(fullId, (entry.id << 1) | (hasData ? 1 : 0));
                NETWORK_LEGACY_MAP.put(entry.id, fullId | (hasData ? 1 : 0));
            }
        }

        ITEM_DATA_PALETTE = paletteBuffer.getBuffer();
    }

    public static int getNetworkFullId(Item item) {
        int fullId = getFullId(item.getId(), item.hasMeta() ? item.getDamage() : -1);
        int networkId = LEGACY_NETWORK_MAP.get(fullId);
        if (networkId == -1) {
            networkId = LEGACY_NETWORK_MAP.get(getFullId(item.getId(), 0));
        }
        if (networkId == -1) {
            throw new IllegalArgumentException("Unknown item mapping " + item.getId() + ":" + item.getDamage());
        }

        return networkId;
    }

    public static int getLegacyFullId(int networkId) {
        int fullId = NETWORK_LEGACY_MAP.get(networkId);
        if (fullId == -1) {
            throw new IllegalArgumentException("Unknown network mapping: " + networkId);
        }
        return fullId;
    }

    public static int getId(int fullId) {
        return (short) (fullId >> 16);
    }

    public static int getData(int fullId) {
        return ((fullId >> 1) & 0x7fff);
    }

    private static int getFullId(int id, int data) {
        return (((short) id) << 16) | ((data & 0x7fff) << 1);
    }

    public static int getNetworkId(int networkFullId) {
        return networkFullId >> 1;
    }

    public static boolean hasData(int id) {
        return (id & 0x1) != 0;
    }

    @ToString
    @RequiredArgsConstructor
    static class Entry {
        String name;
        int id;
        Integer oldId;
        Integer oldData;
    }
}
