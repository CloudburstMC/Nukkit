package cn.nukkit.item;

import cn.nukkit.item.RuntimeItemMapping.LegacyEntry;
import cn.nukkit.Server;
import cn.nukkit.level.GlobalBlockPalette;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@UtilityClass
public class RuntimeItems {

    private static final Map<String, Integer> legacyString2LegacyInt = new HashMap<>();
    private static RuntimeItemMapping itemPalette;
    private static boolean initialized;

    public static void init() {
        if (initialized) {
            throw new IllegalStateException("RuntimeItems were already generated!");
        }
        initialized = true;
        log.info("Loading runtime items...");

        InputStream itemIdsStream = Server.class.getClassLoader().getResourceAsStream("legacy_item_ids.json");
        if (itemIdsStream == null) {
            throw new AssertionError("Unable to load legacy_item_ids.json");
        }

        JsonObject json = JsonParser.parseReader(new InputStreamReader(itemIdsStream)).getAsJsonObject();
        for (String identifier : json.keySet()) {
            legacyString2LegacyInt.put(identifier, json.get(identifier).getAsInt());
        }

        InputStream mappingStream = Server.class.getClassLoader().getResourceAsStream("item_mappings.json");
        if (mappingStream == null) {
            throw new AssertionError("Unable to load item_mappings.json");
        }
        JsonObject itemMapping = JsonParser.parseReader(new InputStreamReader(mappingStream)).getAsJsonObject();

        Map<String, MappingEntry> mappingEntries = new HashMap<>();
        for (String legacyName : itemMapping.keySet()) {
            JsonObject convertData = itemMapping.getAsJsonObject(legacyName);
            for (String damageStr : convertData.keySet()) {
                String identifier = convertData.get(damageStr).getAsString();
                int damage = Integer.parseInt(damageStr);
                mappingEntries.put(identifier, new MappingEntry(legacyName, damage));
            }
        }
        itemPalette = new RuntimeItemMapping(mappingEntries);
    }

    public static Item parseCreativeItem(JsonObject json, boolean ignoreUnknown) {
        String identifier = json.get("id").getAsString();
        LegacyEntry legacyEntry = itemPalette.fromIdentifier(identifier);
        if (legacyEntry == null) {
            if (!ignoreUnknown) {
                throw new IllegalStateException("Can not find legacyEntry for " + identifier);
            }
            log.debug("Can not find legacyEntry for " + identifier);
            return null;
        }

        byte[] nbtBytes;
        if (json.has("nbt_b64")) {
            nbtBytes = Base64.getDecoder().decode(json.get("nbt_b64").getAsString());
        } else {
            nbtBytes = new byte[0];
        }

        int legacyId = legacyEntry.getLegacyId();
        int damage = 0;
        if (json.has("damage")) {
            damage = json.get("damage").getAsInt();
        } else if (legacyEntry.isHasDamage()) {
            damage = legacyEntry.getDamage();
        } else if (json.has("blockRuntimeId")) {
            int runtimeId = json.get("blockRuntimeId").getAsInt();
            int fullId = GlobalBlockPalette.getLegacyFullId(runtimeId);
            if (fullId == -1) {
                if (ignoreUnknown) {
                    return null;
                } else {
                    throw new IllegalStateException("Can not find blockRuntimeId for " + runtimeId);
                }
            }

            damage = fullId & 0xf;
        }

        int count = json.has("count") ? json.get("count").getAsInt() : 1;
        return Item.get(legacyId, damage, count, nbtBytes);
    }

    public static RuntimeItemMapping getMapping() {
        return itemPalette;
    }

    public static int getLegacyIdFromLegacyString(String identifier) {
        return legacyString2LegacyInt.getOrDefault(identifier, -1);
    }

    @Data
    public static class MappingEntry {
        private final String legacyName;
        private final int damage;
    }
}
