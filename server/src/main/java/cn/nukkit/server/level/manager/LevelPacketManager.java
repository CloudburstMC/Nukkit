/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.level.manager;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.server.entity.BaseEntity;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.packet.LevelEventPacket;
import cn.nukkit.server.network.minecraft.session.PlayerSession;
import com.flowpowered.math.vector.Vector3f;
import com.google.common.base.Preconditions;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Log4j2
public class LevelPacketManager {
    private final Queue<MinecraftPacket> broadcastQueue = new ConcurrentLinkedQueue<>();
    private final Map<Vector3f, Queue<MinecraftPacket>> specificPositionViewerQueue = new HashMap<>();
    private final TLongObjectMap<Queue<MinecraftPacket>> specificEntityViewerQueue = new TLongObjectHashMap<>();
    private final int viewDistanceSquared;
    private final NukkitLevel level;


    public LevelPacketManager(NukkitLevel level, int viewDistance) {
        this.level = level;
        this.viewDistanceSquared = (int) Math.pow((viewDistance * 16), 2);
    }

    public void onTick() {
        List<PlayerSession> playersInWorld = level.getEntityManager().getPlayers();
        MinecraftPacket pk;
        while ((pk = broadcastQueue.poll()) != null) {
            for (PlayerSession session : playersInWorld) {
                if (!session.isRemoved()) {
                    session.getMinecraftSession().addToSendQueue(pk);
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
                            for (MinecraftPacket packet : queue) {

                                session.getMinecraftSession().addToSendQueue(packet);
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
                        log.debug("Sending packets to {} from position {}", session.getName(), position);
                        for (MinecraftPacket packet : queue) {
                            session.getMinecraftSession().addToSendQueue(packet);
                        }
                    }
                }
            });
            specificPositionViewerQueue.clear();
        }
    }


    @Synchronized("specificEntityViewerQueue")
    public void queuePacketForViewers(Entity entity, MinecraftPacket packet) {
        Preconditions.checkNotNull(entity, "entity");
        Preconditions.checkNotNull(packet, "packet");
        Queue<MinecraftPacket> packageQueue = specificEntityViewerQueue.get(entity.getEntityId());
        if (packageQueue == null) {
            specificEntityViewerQueue.put(entity.getEntityId(), packageQueue = new ArrayDeque<>());
        }
        packageQueue.add(packet);
    }

    @Synchronized("specificPositionViewerQueue")
    public void queuePacketForViewers(Vector3f position, MinecraftPacket packet) {
        Preconditions.checkNotNull(position, "position");
        Preconditions.checkNotNull(packet, "packet");
        Queue<MinecraftPacket> packageQueue = specificPositionViewerQueue.get(position);
        if (packageQueue == null) {
            specificPositionViewerQueue.put(position, packageQueue = new ArrayDeque<>());
        }
        packageQueue.add(packet);
    }

    public void queuePacketForPlayers(MinecraftPacket packet) {
        Preconditions.checkNotNull(packet, "packet");
        broadcastQueue.add(packet);
    }

    public void queueLevelEvent(LevelEventPacket.Event event, Vector3f position, int data) {
        LevelEventPacket packet = new LevelEventPacket();
        packet.setEvent(event);
        packet.setPosition(position);
        packet.setData(data);

        queuePacketForViewers(position, packet);
    }
}
