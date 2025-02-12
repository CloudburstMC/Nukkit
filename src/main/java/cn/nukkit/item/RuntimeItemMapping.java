package cn.nukkit.item;

import cn.nukkit.Nukkit;
import cn.nukkit.block.Block;
import cn.nukkit.item.RuntimeItems.MappingEntry;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.ItemComponentPacket;
import cn.nukkit.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

@Log4j2
public class RuntimeItemMapping {

    private final Int2ObjectMap<LegacyEntry> runtime2Legacy = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<RuntimeEntry> legacy2Runtime = new Int2ObjectOpenHashMap<>();
    private final Map<String, LegacyEntry> identifier2Legacy = new HashMap<>();

    private final Map<String, ItemComponentPacket.ItemDefinition> vanillaItems = new HashMap<>();

    public RuntimeItemMapping(Map<String, MappingEntry> mappings) {
        JsonArray json = Utils.loadJsonResource("runtime_item_states.json").getAsJsonArray();
        if (json.isEmpty()) {
            throw new IllegalStateException("Empty array");
        }

        CompoundTag itemComponents;
        try (InputStream stream = RuntimeItemMapping.class.getClassLoader().getResourceAsStream("item_components.nbt")) {
            itemComponents = NBTIO.read(new BufferedInputStream(new GZIPInputStream(stream)), ByteOrder.BIG_ENDIAN, false);
        } catch (Exception e) {
            throw new AssertionError("Error while loading item_components.nbt", e);
        }

        for (JsonElement element : json) {
            if (!element.isJsonObject()) {
                throw new IllegalStateException("Invalid entry");
            }
            JsonObject entry = element.getAsJsonObject();
            String identifier = entry.get("name").getAsString();
            int runtimeId = entry.get("id").getAsInt();

            int version = entry.get("version").getAsInt();
            boolean componentBased = entry.get("componentBased").getAsBoolean();
            CompoundTag components = (CompoundTag) itemComponents.get(identifier);
            this.vanillaItems.put(identifier, new ItemComponentPacket.ItemDefinition(identifier, runtimeId, componentBased, version, components));

            boolean hasDamage = false;
            int damage = 0;
            int legacyId;

            if (mappings.containsKey(identifier)) {
                MappingEntry mapping = mappings.get(identifier);
                legacyId = RuntimeItems.getLegacyIdFromLegacyString(mapping.getLegacyName());
                if (legacyId == -1) {
                    throw new IllegalStateException("Unable to match " + mapping + " with legacyId");
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

            this.registerItem(identifier, runtimeId, legacyId, damage, hasDamage);
        }
    }

    public void registerItem(String identifier, int runtimeId, int legacyId, int damage) {
        this.registerItem(identifier, runtimeId, legacyId, damage, false);
    }

    public void registerItem(String identifier, int runtimeId, int legacyId, int damage, boolean hasDamage) {
        int fullId = this.getFullId(legacyId, damage);
        LegacyEntry legacyEntry = new LegacyEntry(legacyId, hasDamage, damage);

        if (Nukkit.DEBUG > 1) {
            if (this.runtime2Legacy.containsKey(runtimeId)) {
                log.warn("RuntimeItemMapping: Registering " + identifier + " but runtime id " + runtimeId + " is already used");
            }
        }

        this.runtime2Legacy.put(runtimeId, legacyEntry);
        this.identifier2Legacy.put(identifier, legacyEntry);
        if (!hasDamage && this.legacy2Runtime.containsKey(fullId)) {
            log.debug("RuntimeItemMapping contains duplicated legacy item state runtimeId=" + runtimeId + " identifier=" + identifier);
        } else {
            this.legacy2Runtime.put(fullId, new RuntimeEntry(identifier, runtimeId, hasDamage));
        }
    }

    public LegacyEntry fromRuntime(int runtimeId) {
        LegacyEntry legacyEntry = this.runtime2Legacy.get(runtimeId);
        if (legacyEntry == null) {
            //throw new IllegalArgumentException("Unknown runtime2Legacy mapping: " + runtimeId);
            log.warn("Unknown runtime2Legacy mapping: " + runtimeId);
            return new LegacyEntry(0, false, 0);
        }
        return legacyEntry;
    }

    public RuntimeEntry toRuntime(int id, int meta) {
        RuntimeEntry runtimeEntry = this.legacy2Runtime.get(this.getFullId(id, meta));
        if (runtimeEntry == null) {
            runtimeEntry = this.legacy2Runtime.get(this.getFullId(id, 0));
        }

        if (runtimeEntry == null) {
            log.warn("Unknown legacy2Runtime mapping: id=" + id + " meta=" + meta);
            runtimeEntry = this.legacy2Runtime.get(this.getFullId(Item.INFO_UPDATE, 0));
            if (runtimeEntry == null) throw new RuntimeException("Runtime ID for Item.INFO_UPDATE must exist!");
            //throw new IllegalArgumentException("Unknown legacy2Runtime mapping: id=" + id + " meta=" + meta);
        }
        return runtimeEntry;
    }

    public Item parseCreativeItem(JsonObject json, boolean ignoreUnknown) {
        String identifier = json.get("id").getAsString();
        LegacyEntry legacyEntry = this.fromIdentifier(identifier);
        if (legacyEntry == null) {
            if (!ignoreUnknown) {
                throw new IllegalStateException("Can not find legacyEntry for " + identifier);
            }
            if (Nukkit.DEBUG > 2) {
                log.debug("Can not find legacyEntry for " + identifier);
            }
            return null;
        }

        byte[] nbtBytes;
        if (json.has("nbt_b64")) {
            nbtBytes = Base64.getDecoder().decode(json.get("nbt_b64").getAsString());
        } else if (json.has("nbt_hex")) {
            nbtBytes = Utils.parseHexBinary(json.get("nbt_hex").getAsString());
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
            if (runtimeId != 0) {
                int fullId = GlobalBlockPalette.getLegacyFullId(runtimeId);
                if (fullId == -1) {
                    if (ignoreUnknown) {
                        return null;
                    } else {
                        throw new IllegalStateException("Can not find blockRuntimeId for " + identifier + " (" + runtimeId + ")");
                    }
                }

                damage = fullId & Block.DATA_MASK;
            }
        }

        int count = json.has("count") ? json.get("count").getAsInt() : 1;
        return Item.get(legacyId, damage, count, nbtBytes);
    }

    public LegacyEntry fromIdentifier(String identifier) {
        return this.identifier2Legacy.get(identifier);
    }

    public int getFullId(int id, int data) {
        return (((short) id) << 16) | ((data & 0x7fff) << 1);
    }

    public Collection<ItemComponentPacket.ItemDefinition> getVanillaItemDefinitions() {
        return vanillaItems.values();
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
