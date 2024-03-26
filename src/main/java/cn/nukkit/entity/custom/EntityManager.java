package cn.nukkit.entity.custom;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.AvailableEntityIdentifiersPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EntityManager {

    private static final EntityManager instance = new EntityManager();

    public static EntityManager get() {
        return instance;
    }

    private final Map<String, EntityDefinition> entityDefinitions = new HashMap<>();
    private final Map<String, EntityDefinition> alternateNameDefinitions = new HashMap<>();
    private final Int2ObjectMap<EntityDefinition> runtimeDefinitions = new Int2ObjectOpenHashMap<>();

    private final Map<String, Integer> vanillaEntitiesMap = new HashMap<>();

    private byte[] networkTagCached;

    public EntityManager() {
        AddEntityPacket.LEGACY_IDS.forEach((id, identifier) -> this.vanillaEntitiesMap.put(identifier, id));
    }

    public void registerDefinition(EntityDefinition definition) {
        if (this.entityDefinitions.containsKey(definition.getIdentifier())) {
            throw new IllegalArgumentException("Custom entity " + definition.getIdentifier() + " was already registered");
        }
        this.entityDefinitions.put(definition.getIdentifier(), definition);
        this.runtimeDefinitions.put(definition.getRuntimeId(), definition);

        if (definition.getAlternateName() != null) {
            this.alternateNameDefinitions.put(definition.getAlternateName(), definition);
        }
    }

    public EntityDefinition getDefinition(String identifier) {
        EntityDefinition definition = this.entityDefinitions.get(identifier);
        if (definition == null) {
            definition = this.alternateNameDefinitions.get(identifier);
        }
        return definition;
    }

    public EntityDefinition getDefinition(int runtimeId) {
        return this.runtimeDefinitions.get(runtimeId);
    }

    public int getRuntimeId(String identifier) {
        EntityDefinition definition = this.entityDefinitions.get(identifier);
        if (definition == null) {
            return this.vanillaEntitiesMap.getOrDefault(identifier, 0);
        }
        return definition.getRuntimeId();
    }

    private void createNetworkTag() {
        try {
            CompoundTag networkTag = (CompoundTag) NBTIO.readNetwork(new ByteArrayInputStream(AvailableEntityIdentifiersPacket.TAG));
            ListTag<CompoundTag> identifiers = networkTag.getList("idlist", CompoundTag.class);

            for (EntityDefinition definition : this.entityDefinitions.values()) {
                if (!definition.isServerSideOnly()) {
                    CompoundTag nbt = definition.getNetworkTag();
                    identifiers.add(nbt);
                }
            }
            networkTag.putList(identifiers);

            this.networkTagCached = NBTIO.writeNetwork(networkTag);
        } catch (IOException e) {
            throw new RuntimeException("Unable to init entityIdentifiers", e);
        }
    }

    public byte[] getNetworkTagCached() {
        if (this.networkTagCached == null) {
            this.createNetworkTag();
        }
        return this.networkTagCached;
    }

    public boolean hasCustomEntities() {
        return !this.entityDefinitions.isEmpty();
    }
}
