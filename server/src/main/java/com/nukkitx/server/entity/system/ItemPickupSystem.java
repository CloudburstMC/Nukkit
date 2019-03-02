package com.nukkitx.server.entity.system;

import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.entity.component.ContainedItem;
import com.nukkitx.api.entity.component.PickupDelay;
import com.nukkitx.api.entity.system.System;
import com.nukkitx.api.entity.system.SystemRunner;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.util.BoundingBox;
import com.nukkitx.protocol.bedrock.packet.TakeItemEntityPacket;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.level.NukkitLevel;

public class ItemPickupSystem implements SystemRunner {
    public static final System SYSTEM = System.builder()
            .expectComponent(ContainedItem.class)
            .expectComponent(PickupDelay.class)
            .runner(new ItemPickupSystem())
            .build();

    @Override
    public void run(Entity entity) {
        if (!entity.ensureAndGet(PickupDelay.class).canPickup()) {
            // Cannot be picked up yet
            return;
        }

        BoundingBox enlargedBB = entity.getBoundingBox().grow(1f);
        ItemStack item = entity.ensureAndGet(ContainedItem.class).getItem();

        for (BaseEntity entityNear : ((NukkitLevel) entity.getLevel()).getEntityManager().getEntitiesInBounds(enlargedBB)) {
            if (entityNear != entity && entityNear.onItemPickup(item)) {
                // Successfully picked up the item.
                TakeItemEntityPacket packet = new TakeItemEntityPacket();
                packet.setItemRuntimeEntityId(entity.getEntityId());
                packet.setRuntimeEntityId(entityNear.getEntityId());
                ((NukkitLevel) entity.getLevel()).getPacketManager().sendImmediatePacketForViewers(entity.getPosition(), packet);
                entity.remove();
                break;
            }
        }
    }
}
