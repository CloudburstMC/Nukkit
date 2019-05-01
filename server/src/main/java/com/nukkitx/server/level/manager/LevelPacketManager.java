package com.nukkitx.server.level.manager;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Preconditions;
import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.level.SoundEvent;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.LevelEventPacket;
import com.nukkitx.protocol.bedrock.packet.LevelSoundEventPacket;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.level.NukkitLevel;
import com.nukkitx.server.network.bedrock.session.PlayerSession;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import lombok.Synchronized;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LevelPacketManager {
    private final Queue<BedrockPacket> broadcastQueue = new ConcurrentLinkedQueue<>();
    private final Map<Vector3f, Queue<BedrockPacket>> specificPositionViewerQueue = new HashMap<>();
    private final TLongObjectMap<Queue<BedrockPacket>> specificEntityViewerQueue = new TLongObjectHashMap<>();
    private final int viewDistanceSquared;
    private final NukkitLevel level;

    public LevelPacketManager(NukkitLevel level, int viewDistance) {
        this.level = level;
        this.viewDistanceSquared = (int) Math.pow((viewDistance << 4), 2);
    }

    public void onTick() {
        List<PlayerSession> playersInWorld = level.getEntityManager().getPlayers();
        BedrockPacket pk;
        while ((pk = broadcastQueue.poll()) != null) {
            for (PlayerSession session : playersInWorld) {
                if (!session.isRemoved()) {
                    session.getBedrockSession().sendPacket(pk);
                }
            }
        }

        synchronized (specificEntityViewerQueue) {
            specificEntityViewerQueue.forEachEntry((eid, queue) -> {
                Optional<BaseEntity> entityById = level.getEntityManager().getEntityById(eid);
                if (entityById.isPresent()) {
                    Entity entity = entityById.get();
                    for (PlayerSession session : playersInWorld) {
                        if (session == entity) continue; // Don't move ourselves

                        if (session.getPosition().distanceSquared(entity.getPosition()) <= viewDistanceSquared && !session.isRemoved()) {
                            for (BedrockPacket packet : queue) {

                                session.getBedrockSession().sendPacket(packet);
                            }
                        }
                    }
                }
                return true;
            });
            specificEntityViewerQueue.clear();
        }

        synchronized (specificPositionViewerQueue) {
            specificPositionViewerQueue.forEach((position, queue) -> {
                for (PlayerSession session : playersInWorld) {
                    if (session.getPosition().distanceSquared(position) <= viewDistanceSquared && !session.isRemoved()) {
                        for (BedrockPacket packet : queue) {
                            session.getBedrockSession().sendPacket(packet);
                        }
                    }
                }
            });
            specificPositionViewerQueue.clear();
        }
    }


    @Synchronized("specificEntityViewerQueue")
    public void queuePacketForViewers(Entity entity, BedrockPacket packet) {
        Preconditions.checkNotNull(entity, "entity");
        Preconditions.checkNotNull(packet, "packet");
        Queue<BedrockPacket> packageQueue = specificEntityViewerQueue.get(entity.getEntityId());
        if (packageQueue == null) {
            specificEntityViewerQueue.put(entity.getEntityId(), packageQueue = new ArrayDeque<>());
        }
        packageQueue.add(packet);
    }

    @Synchronized("specificPositionViewerQueue")
    public void queuePacketForViewers(Vector3f position, BedrockPacket packet) {
        Preconditions.checkNotNull(position, "blockPosition");
        Preconditions.checkNotNull(packet, "packet");
        Queue<BedrockPacket> packageQueue = specificPositionViewerQueue.get(position);
        if (packageQueue == null) {
            specificPositionViewerQueue.put(position, packageQueue = new ArrayDeque<>());
        }
        packageQueue.add(packet);
    }

    public void queuePacketForPlayers(BedrockPacket packet) {
        Preconditions.checkNotNull(packet, "packet");
        broadcastQueue.add(packet);
    }

    public void queueEventForViewers(Vector3f position, LevelEventPacket.Event event, int data) {
        LevelEventPacket packet = new LevelEventPacket();
        packet.setEvent(event);
        packet.setPosition(position);
        packet.setData(data);

        queuePacketForViewers(position, packet);
    }

    public void queueEventForViewers(Entity entity, LevelEventPacket.Event event, int data) {
        LevelEventPacket packet = new LevelEventPacket();
        packet.setEvent(event);
        packet.setPosition(entity.getPosition());
        packet.setData(data);

        queuePacketForViewers(entity, packet);
    }

    public void queueSoundForViewers(Vector3f position, SoundEvent sound, int pitch, int extraData, boolean disableRelativeVolume) {
        LevelSoundEventPacket packet = new LevelSoundEventPacket();
        packet.setPosition(position);
        packet.setSound(com.nukkitx.protocol.bedrock.data.SoundEvent.valueOf(sound.name()));
        packet.setPitch(pitch);
        packet.setExtraData(extraData);
        packet.setRelativeVolumeDisabled(disableRelativeVolume);

        queuePacketForViewers(position, packet);
    }

    public void queueSoundForViewers(Entity entity, SoundEvent sound, int pitch, int extraData, boolean disableRelativeVolume) {
        LevelSoundEventPacket packet = new LevelSoundEventPacket();
        packet.setPosition(entity.getPosition());
        packet.setSound(com.nukkitx.protocol.bedrock.data.SoundEvent.valueOf(sound.name()));
        packet.setPitch(pitch);
        packet.setExtraData(extraData);
        packet.setRelativeVolumeDisabled(disableRelativeVolume);

        queuePacketForViewers(entity, packet);
    }

    public void sendImmediatePacketForViewers(Entity entity, BedrockPacket packet) {
        Preconditions.checkNotNull(entity, "entity");
        Preconditions.checkNotNull(packet, "packet");

        List<PlayerSession> playersInWorld = level.getEntityManager().getPlayers();

        for (PlayerSession session : playersInWorld) {
            if (session == entity) continue; // Don't move ourselves

            if (session.getPosition().distanceSquared(entity.getPosition()) <= viewDistanceSquared && !session.isRemoved()) {
                session.getBedrockSession().sendPacketImmediately(packet);
            }
        }
    }

    public void sendImmediatePacketForViewers(Vector3f position, BedrockPacket packet) {
        Preconditions.checkNotNull(position, "blockPosition");
        Preconditions.checkNotNull(packet, "packet");

        List<PlayerSession> playersInWorld = level.getEntityManager().getPlayers();

        for (PlayerSession session : playersInWorld) {
            if (session.getPosition().distanceSquared(position) <= viewDistanceSquared && !session.isRemoved()) {
                session.getBedrockSession().sendPacketImmediately(packet);
            }
        }
    }
}
