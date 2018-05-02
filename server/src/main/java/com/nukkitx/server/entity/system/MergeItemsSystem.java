package com.nukkitx.server.entity.system;

import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.entity.component.ContainedItem;
import com.nukkitx.api.entity.misc.DroppedItem;
import com.nukkitx.api.entity.system.System;
import com.nukkitx.api.entity.system.SystemRunner;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.util.BoundingBox;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.entity.EntityEvent;
import com.nukkitx.server.level.NukkitLevel;
import com.nukkitx.server.network.minecraft.packet.EntityEventPacket;

public class MergeItemsSystem implements SystemRunner {
    public static final System SYSTEM = System.builder()
            .runner(new MergeItemsSystem())
            .expectComponent(ContainedItem.class)
            .build();

    @Override
    public void run(Entity entity) {
        if (!(entity instanceof DroppedItem)) {
            return;
        }
        ContainedItem containedItem = entity.ensureAndGet(ContainedItem.class);
        ItemInstance itemInstance = containedItem.getItem();
        if (itemInstance.isFull()) {
            return;
        }

        BoundingBox enlargedBB = entity.getBoundingBox().grow(1f);

        for (BaseEntity bbEntity : ((NukkitLevel) entity.getLevel()).getEntityManager().getEntitiesInBounds(enlargedBB)) {
            if (bbEntity == entity) {
                continue;
            }
            if (bbEntity instanceof DroppedItem) {
                ContainedItem containedItem1 = bbEntity.ensureAndGet(ContainedItem.class);
                if (itemInstance.isMergeable(containedItem1.getItem())) {
                    int newAmount = itemInstance.getAmount() + containedItem1.getItem().getAmount();

                    // Check if a merge is possible
                    if (newAmount > itemInstance.getItemType().getMaximumStackSize()) {
                        continue;
                    }

                    containedItem.setItem(itemInstance.toBuilder().amount(newAmount).build());

                    // Send merge to client
                    EntityEventPacket entityEvent = new EntityEventPacket();
                    entityEvent.setRuntimeEntityId(entity.getEntityId());
                    entityEvent.setData(newAmount);
                    entityEvent.setEvent(EntityEvent.ITEM_ENTITY_MERGE);

                    ((NukkitLevel) entity.getLevel()).getPacketManager().queuePacketForViewers(entity, entityEvent);
                    // Remove entity.
                    bbEntity.remove();
                    // Set position to midpoint
                    entity.setPositionFromSystem(entity.getPosition().add(bbEntity.getPosition()).div(2f).add(0,0.5f, 0));
                    break;
                }
            }
        }
    }
}
