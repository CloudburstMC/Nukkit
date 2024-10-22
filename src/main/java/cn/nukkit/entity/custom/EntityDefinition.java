package cn.nukkit.entity.custom;

import cn.nukkit.entity.Entity;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Builder;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class EntityDefinition {

    public static final AtomicInteger ID_ALLOCATOR = new AtomicInteger(10000);

    private final String identifier;
    private final String parentEntity;
    private final boolean spawnEgg;

    private final String alternateName;
    private final Class<? extends Entity> implementation;

    private final boolean serverSideOnly;
    private final int runtimeId;

    private CompoundTag networkTag;

    @Builder
    public EntityDefinition(String identifier, String parentEntity, boolean spawnEgg, String alternateName, Class<? extends Entity> implementation, boolean serverSideOnly) {
        if (!CustomEntity.class.isAssignableFrom(implementation)) {
            throw new IllegalArgumentException("Implementation class must implement CustomEntity interface");
        }

        if (serverSideOnly && parentEntity == null) {
            throw new IllegalArgumentException("Server side entity must have parent entity set");
        }

        this.identifier = identifier;
        this.parentEntity = parentEntity;
        this.spawnEgg = spawnEgg;
        this.alternateName = alternateName;
        this.implementation = implementation;
        this.serverSideOnly = serverSideOnly;

        if (this.serverSideOnly) {
            int runtimeId = EntityManager.get().getRuntimeId(this.parentEntity);
            if (runtimeId == 0) {
                throw new IllegalArgumentException("Unknown entity type " + this.parentEntity);
            }
            this.runtimeId = runtimeId;
        } else {
            this.runtimeId = ID_ALLOCATOR.getAndIncrement();
        }
    }

    private CompoundTag createNetworkTag() {
        CompoundTag nbt = new CompoundTag("");
        nbt.putBoolean("hasspawnegg", this.spawnEgg);
        nbt.putBoolean("summonable", true);
        nbt.putString("id", this.identifier);
        nbt.putString("bid", this.parentEntity == null ? "" : this.parentEntity);
        nbt.putInt("rid", this.runtimeId);
        return nbt;
    }

    public CompoundTag getNetworkTag() {
        if (this.networkTag == null) {
            this.networkTag = this.createNetworkTag();
        }
        return this.networkTag;
    }
}
