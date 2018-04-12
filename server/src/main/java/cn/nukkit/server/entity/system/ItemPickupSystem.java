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

package cn.nukkit.server.entity.system;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.entity.component.ContainedItem;
import cn.nukkit.api.entity.component.PickupDelay;
import cn.nukkit.api.entity.misc.DroppedItem;
import cn.nukkit.api.entity.system.System;
import cn.nukkit.api.entity.system.SystemRunner;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.util.BoundingBox;
import cn.nukkit.server.entity.BaseEntity;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.network.minecraft.packet.TakeItemEntityPacket;

public class ItemPickupSystem implements SystemRunner {
    public static final System SYSTEM = System.builder()
            .expectComponent(ContainedItem.class)
            .expectComponent(PickupDelay.class)
            .runner(new ItemPickupSystem())
            .build();

    @Override
    public void run(Entity entity) {
        if (!(entity instanceof DroppedItem)) {
            return;
        }

        if (!entity.ensureAndGet(PickupDelay.class).canPickup()) {
            // Cannot be picked up yet
            return;
        }

        BoundingBox enlargedBB = entity.getBoundingBox().grow(1f);
        ItemInstance item = entity.ensureAndGet(ContainedItem.class).getItem();

        for (BaseEntity entityNear : ((NukkitLevel) entity.getLevel()).getEntityManager().getEntitiesInBounds(enlargedBB)) {
            if (entityNear.onItemPickup(item)) {
                // Successfully picked up the item.
                TakeItemEntityPacket packet = new TakeItemEntityPacket();
                packet.setItemRuntimeEntityId(entity.getEntityId());
                packet.setRuntimeEntityId(entityNear.getEntityId());
                ((NukkitLevel) entity.getLevel()).getPacketManager().queuePacketForViewers(entity, packet);
                entity.remove();
            }
        }
    }
}
