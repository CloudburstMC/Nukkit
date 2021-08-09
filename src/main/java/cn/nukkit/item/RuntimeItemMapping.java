package cn.nukkit.item;

import cn.nukkit.item.RuntimeItems.MappingEntry;
import cn.nukkit.Server;
import cn.nukkit.utils.BinaryStream;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class RuntimeItemMapping {

    private final Int2ObjectMap<LegacyEntry> runtime2Legacy = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<RuntimeEntry> legacy2Runtime = new Int2ObjectOpenHashMap<>();
    private final Map<String, LegacyEntry> identifier2Legacy = new HashMap<>();

    private byte[] itemPalette;

    public RuntimeItemMapping(Map<String, MappingEntry> mappings) {
        InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_item_states.json");
        if (stream == null) {
            throw new AssertionError("Unable to load runtime_item_states.json");
        }
        JsonArray json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonArray();

        for (JsonElement element : json) {
            if (!element.isJsonObject()) {
                throw new IllegalStateException("Invalid entry");
            }
            JsonObject entry = element.getAsJsonObject();
            String identifier = entry.get("name").getAsString();
            int runtimeId = entry.get("id").getAsInt();

            boolean hasDamage = false;
            int damage = 0;
            int legacyId;

            if (mappings.containsKey(identifier)) {
                MappingEntry mapping = mappings.get(identifier);
                legacyId = RuntimeItems.getLegacyIdFromLegacyString(mapping.getLegacyName());
                if (legacyId == -1) {
                    throw new IllegalStateException("Unable to match  " + mapping + " with legacyId");
                }
                damage = mapping.getDamage();
                hasDamage = true;
            } else {
                legacyId = RuntimeItems.getLegacyIdFromLegacyString(identifier);
                if (legacyId == -1) {
                    log.trace("Unable to find legacyId for " + identifier);
                    continue;
                }
            }

            int fullId = this.getFullId(legacyId, damage);
            LegacyEntry legacyEntry = new LegacyEntry(legacyId, hasDamage, damage);

            this.runtime2Legacy.put(runtimeId, legacyEntry);
            this.identifier2Legacy.put(identifier, legacyEntry);
            this.legacy2Runtime.put(fullId, new RuntimeEntry(identifier, runtimeId, hasDamage));
        }

        this.generatePalette();
    }

    private void generatePalette() {
        BinaryStream paletteBuffer = new BinaryStream();
        paletteBuffer.putUnsignedVarInt(this.legacy2Runtime.size());
        for (RuntimeEntry entry : this.legacy2Runtime.values()) {
            paletteBuffer.putString(entry.getIdentifier());
            paletteBuffer.putLShort(entry.getRuntimeId());
            paletteBuffer.putBoolean(false); // Component item
        }
        this.itemPalette = paletteBuffer.getBuffer();
    }

    public LegacyEntry fromRuntime(int runtimeId) {
        LegacyEntry legacyEntry = this.runtime2Legacy.get(runtimeId);
        if (legacyEntry == null) {
            throw new IllegalArgumentException("Unknown runtime2Legacy mapping: " + runtimeId);
        }
        return legacyEntry;
    }

    public RuntimeEntry toRuntime(int id, int meta) {
        RuntimeEntry runtimeEntry = this.legacy2Runtime.get(this.getFullId(id, meta));
        if (runtimeEntry == null) {
            runtimeEntry = this.legacy2Runtime.get(this.getFullId(id, 0));
        }

        if (runtimeEntry == null) {
            throw new IllegalArgumentException("Unknown legacy2Runtime mapping: id=" + id + " meta=" + meta);
        }
        return runtimeEntry;
    }

    public LegacyEntry fromIdentifier(String identifier) {
        return this.identifier2Legacy.get(identifier);
    }

    public int getFullId(int id, int data) {
        return (((short) id) << 16) | ((data & 0x7fff) << 1);
    }

    public byte[] getItemPalette() {
        return this.itemPalette;
    }

    @Data
    public static class LegacyEntry {
        private final int legacyId;
        private final boolean hasDamage;
        private final int damage;

        public int getDamage() {
            return this.hasDamage ? this.damage : 0;
        }
    }

    @Data
    public static class RuntimeEntry {
        private final String identifier;
        private final int runtimeId;
        private final boolean hasDamage;
    }
}
