package com.nukkitx.server.level.manager;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.entity.system.System;
import com.nukkitx.api.util.BoundingBox;
import com.nukkitx.protocol.bedrock.packet.MoveEntityAbsolutePacket;
import com.nukkitx.protocol.bedrock.packet.SetEntityMotionPacket;
import com.nukkitx.protocol.bedrock.session.BedrockSession;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.level.NukkitLevel;
import com.nukkitx.server.network.bedrock.session.NukkitPlayerSession;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Log4j2
public class LevelEntityManager {
    private final NukkitLevel level;
    private final TLongObjectMap<BaseEntity> entities = new TLongObjectHashMap<>();
    private final List<System> systems = new CopyOnWriteArrayList<>();
    private final AtomicLong entityIdAllocator = new AtomicLong(0);
    private final AtomicBoolean entitiesChanged = new AtomicBoolean(false);
    private final AtomicBoolean entitiesTicking = new AtomicBoolean(false);

    public LevelEntityManager(NukkitLevel level) {
        this.level = level;
    }

    public void registerEntity(BaseEntity entity) {
        synchronized (entities) {
            entities.put(entity.getEntityId(), entity);
            entitiesChanged.set(true);
        }
    }

    public void deregisterEntity(BaseEntity entity) {
        synchronized (entities) {
            entities.remove(entity.getEntityId());
            entitiesChanged.set(true);
        }
    }

    public void registerSystem(System system) {
        Preconditions.checkNotNull(system, "system");
        systems.add(system);
    }

    public boolean deregisterSystem(System system) {
        Preconditions.checkNotNull(system, "system");
        return systems.remove(system);
    }

    public long allocateEntityId() {
        return entityIdAllocator.incrementAndGet();
    }

    public Optional<BaseEntity> getEntityById(long entityId) {
        synchronized (entities) {
            return Optional.ofNullable(entities.get(entityId));
        }
    }

    public List<NukkitPlayerSession> getPlayers() {
        synchronized (entities) {
            List<NukkitPlayerSession> sessions = new ArrayList<>();
            entities.forEachValue(entity -> {
                if (entity instanceof NukkitPlayerSession) {
                    NukkitPlayerSession session = (NukkitPlayerSession) entity;
                    BedrockSession bedrockSession = session.getBedrockSession();
                    if (bedrockSession != null && !bedrockSession.isClosed()) {
                        sessions.add((NukkitPlayerSession) entity);
                    }
                }
                return true;
            });
            return sessions;
        }
    }

    public Collection<BaseEntity> getAllEntities() {
        synchronized (entities) {
            return ImmutableList.copyOf(entities.valueCollection());
        }
    }

    public Collection<BaseEntity> getEntitiesInDistance(Vector3f origin, float distance) {
        synchronized (entities) {
            Collection<BaseEntity> inDistance = new ArrayList<>();
            entities.forEachValue(entity -> {
                if (!entity.isRemoved() && entity.getPosition().distance(origin) <= distance) {
                    inDistance.add(entity);
                }
                return true;
            });
            return inDistance;
        }
    }

    public Collection<BaseEntity> getEntitiesInBounds(BoundingBox boundingBox) {
        synchronized (entities) {
            Collection<BaseEntity> inDistance = new ArrayList<>();
            entities.forEachValue(entity -> {
                if (!entity.isRemoved() && boundingBox.intersectsWith(entity.getBoundingBox())) {
                    inDistance.add(entity);
                }
                return true;
            });
            return inDistance;
        }
    }

    public List<Entity> getEntitiesInChunk(int x, int z) {
        synchronized (entities) {
            List<Entity> foundEntities = new ArrayList<>();
            entities.forEachValue(entity -> {
                int entityChunkX = entity.getPosition().getFloorX() >> 4;
                int entityChunkZ = entity.getPosition().getFloorZ() >> 4;

                if (!entity.isRemoved() && entityChunkX == x && entityChunkZ == z) {
                    foundEntities.add(entity);
                }
                return true;
            });
            return foundEntities;
        }
    }

    public void onTick() {
        entitiesTicking.set(true);

        TLongObjectMap<BaseEntity> entitiesCopy;
        synchronized (entities) {
            entitiesCopy = new TLongObjectHashMap<>(entities);
        }

        try {
            for (TLongObjectIterator<BaseEntity> it = entitiesCopy.iterator(); it.hasNext(); ) {
                it.advance();
                BaseEntity entity = it.value();
                try {
                    if (entity.isRemoved()) {
                        if (log.isDebugEnabled()) {
                            log.debug("{} was removed", entity);
                        }
                        entitiesChanged.set(true);
                        synchronized (entities) {
                            entities.remove(entity.getEntityId());
                        }
                        continue;
                    }

                    for (System system : systems) {
                        if (!system.isSystemCompatible(entity)) {
                            continue;
                        }
                        system.getRunner().run(entity);
                    }

                    // Check if entity has been removed after ticking systems.
                    if (entity.isRemoved()) {
                        if (log.isDebugEnabled()) {
                            log.debug("{} was removed after systems ticked", entity);
                        }
                        entitiesChanged.set(true);
                        synchronized (entities) {
                            entities.remove(entity.getEntityId());
                        }
                        continue;
                    }

                    // Check if we need to send movement updates
                    if (entity.isMovementStale()) {
                        MoveEntityAbsolutePacket moveEntity = new MoveEntityAbsolutePacket();
                        moveEntity.setRuntimeEntityId(entity.getEntityId());
                        moveEntity.setPosition(entity.getGamePosition());
                        moveEntity.setRotation(entity.getRotation().toVector3f());
                        moveEntity.setOnGround(entity.isOnGround());
                        level.getPacketManager().queuePacketForViewers(entity, moveEntity);

                        SetEntityMotionPacket entityMotion = new SetEntityMotionPacket();
                        entityMotion.setRuntimeEntityId(entity.getEntityId());
                        entityMotion.setMotion(entity.getMotion());
                        level.getPacketManager().queuePacketForViewers(entity, entityMotion);

                        entity.resetStaleMovement();
                    }

                } catch (Exception e) {
                    synchronized (entities) {
                        entities.remove(entity.getEntityId());
                    }
                    entitiesChanged.set(true);
                    entity.remove();
                    log.error("Unable to tick entity {}", entity, e);
                }
            }
        } finally {
            entitiesTicking.set(false);
        }

        // Update viewable entities if something changed
        if (entitiesChanged.compareAndSet(true, false)) {
            List<NukkitPlayerSession> players = getPlayers();
            players.forEach(NukkitPlayerSession::updateViewableEntities);
        }
    }

    public boolean isTicking() {
        return entitiesTicking.get();
    }
}
