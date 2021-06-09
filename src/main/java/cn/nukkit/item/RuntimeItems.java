package cn.nukkit.item;

import cn.nukkit.Server;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


@UtilityClass
public class RuntimeItems {

    private static final Map<String, Integer> legacyString2LegacyInt = new HashMap<>();
    private static final RuntimeItemMapping itemPalette;

    static {
        Server.getInstance().getLogger().debug("Loading runtime items...");

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
