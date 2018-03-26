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
