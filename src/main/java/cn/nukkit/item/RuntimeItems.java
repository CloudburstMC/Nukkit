package cn.nukkit.item;

import cn.nukkit.utils.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@UtilityClass
public class RuntimeItems {

    private static final Map<String, Integer> legacyString2LegacyInt = new HashMap<>();
    private static final Map<Integer, String> legacyInt2LegacyString = new HashMap<>();
    private static final Map<String, MappingEntry> latestIdentifierMapping = new HashMap<>();

    private static RuntimeItemMapping mapping;

    private static boolean initialized;

    public static void init() {
        if (initialized) {
            throw new IllegalStateException("RuntimeItems were already generated!");
        }
        initialized = true;
        log.debug("Loading runtime items...");

        JsonObject json = Utils.loadJsonResource("legacy_item_ids.json").getAsJsonObject();
        for (Map.Entry<String, JsonElement> identifierToId : json.entrySet()) {
            int legacyId = identifierToId.getValue().getAsInt();
            legacyString2LegacyInt.put(identifierToId.getKey(), legacyId);
            legacyInt2LegacyString.put(legacyId, identifierToId.getKey());
        }

        JsonObject itemMapping = Utils.loadJsonResource("item_mappings.json").getAsJsonObject();
        Map<String, MappingEntry> mappingEntries = latestIdentifierMapping; // keep it same with master
        for (String legacyName : itemMapping.keySet()) {
            JsonObject convertData = itemMapping.getAsJsonObject(legacyName);
            for (String damageStr : convertData.keySet()) {
                String identifier = convertData.get(damageStr).getAsString();
                int damage = Integer.parseInt(damageStr);
                mappingEntries.put(identifier, new MappingEntry(legacyName, damage));
            }
        }

        mapping = new RuntimeItemMapping(mappingEntries);
    }

    public static RuntimeItemMapping getMapping() {
        return mapping;
    }

    public static int getLegacyIdFromLegacyString(String identifier) {
        return legacyString2LegacyInt.getOrDefault(identifier, -1);
    }

    public static String getLegacyStringFromLegacyId(int legacyId) {
        return legacyInt2LegacyString.getOrDefault(legacyId, "minecraft:info_update");
    }

    public static String getLegacyStringFromNew(String identifier) {
        MappingEntry entry = latestIdentifierMapping.get(identifier);
        return entry == null ? identifier : entry.getLegacyName();
    }

    @Data
    public static class MappingEntry {
        private final String legacyName;
        private final int damage;
    }

    public static int getId(int fullId) {
        return (short) (fullId >> 16);
    }

    public static int getData(int fullId) {
        return ((fullId >> 1) & 0x7fff);
    }

    public static int getFullId(int id, int data) {
        return (((short) id) << 16) | ((data & 0x7fff) << 1);
    }

    public static int getNetworkId(int networkFullId) {
        return networkFullId >> 1;
    }

    public static boolean hasData(int id) {
        return (id & 0x1) != 0;
    }
}
