package cn.nukkit.entity.custom;

import cn.nukkit.Nukkit;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.AvailableEntityIdentifiersPacket;
import cn.nukkit.network.protocol.BatchPacket;
import com.google.common.io.ByteStreams;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;

/**
 * Handles custom entity registry.
 * <p>
 * See <a href="https://github.com/PetteriM1/CustomEntityExample">CustomEntityExample</a> for example usage
 */
public class EntityManager {

    private static final EntityManager INSTANCE = new EntityManager();

    /**
     * Get EntityManager instance
     */
    public static EntityManager get() {
        return INSTANCE;
    }

    private final Map<String, EntityDefinition> entityDefinitions = new HashMap<>();
    private final Map<String, EntityDefinition> alternateNameDefinitions = new HashMap<>();
    private final Int2ObjectMap<EntityDefinition> runtimeDefinitions = new Int2ObjectOpenHashMap<>();

    private final byte[] vanillaTag;
    private final Map<String, Integer> vanillaEntitiesMap = new HashMap<>();

    private volatile boolean closed;

    private BatchPacket cachedPacket;

    private EntityManager() {
        try (InputStream inputStream = Nukkit.class.getClassLoader().getResourceAsStream("entity_identifiers.dat")) {
            if (inputStream == null) throw new AssertionError("Could not find entity_identifiers.dat");
            this.vanillaTag = ByteStreams.toByteArray(inputStream);
        } catch (Exception e) {
            throw new AssertionError("Error whilst loading entity_identifiers.dat", e);
        }

        AddEntityPacket.LEGACY_IDS.forEach((id, identifier) -> this.vanillaEntitiesMap.put(identifier, id));
    }

    public BatchPacket getCachedPacket() {
        if (this.cachedPacket == null) {
            AvailableEntityIdentifiersPacket pk = new AvailableEntityIdentifiersPacket();
            pk.tryEncode();
            this.cachedPacket = pk.compress(Deflater.BEST_COMPRESSION);
        }
        return this.cachedPacket;
    }

    @SuppressWarnings("unused")
    public void registerDefinition(EntityDefinition definition) {
        if (this.closed) {
            throw new IllegalStateException("Entity registry was already closed");
        }

        if (this.entityDefinitions.containsKey(definition.getIdentifier())) {
            throw new IllegalArgumentException("Custom entity " + definition.getIdentifier() + " was already registered");
        }

        this.entityDefinitions.put(definition.getIdentifier(), definition);
        this.runtimeDefinitions.put(definition.getRuntimeId(), definition);

        if (definition.getAlternateName() != null) {
            this.alternateNameDefinitions.put(definition.getAlternateName(), definition);
        }

        this.cachedPacket = null;
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

    public byte[] createNetworkTag() {
        try {
            CompoundTag networkTag = (CompoundTag) NBTIO.readNetwork(new ByteArrayInputStream(this.vanillaTag));
            ListTag<CompoundTag> identifiers = networkTag.getList("idlist", CompoundTag.class);

            for (EntityDefinition definition : this.entityDefinitions.values()) {
                if (!definition.isServerSideOnly()) {
                    identifiers.add(definition.getNetworkTag());
                }
            }

            networkTag.putList(identifiers);

            return NBTIO.writeNetwork(networkTag);
        } catch (IOException e) {
            throw new RuntimeException("Unable to init entityIdentifiers", e);
        }
    }

    @SuppressWarnings("unused")
    public boolean hasCustomEntities() {
        return !this.entityDefinitions.isEmpty();
    }

    /**
     * Internal: close registry to prepare for data generation
     */
    public void closeRegistry() {
        if (this.closed) {
            throw new IllegalStateException("Entity registry was already closed");
        }

        this.closed = true;

        this.getCachedPacket();
    }
}
