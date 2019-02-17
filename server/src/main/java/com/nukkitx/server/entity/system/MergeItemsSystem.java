package com.nukkitx.server.entity.system;

import com.nukkitx.api.entity.Entity;
import com.nukkitx.api.entity.component.ContainedItem;
import com.nukkitx.api.entity.misc.DroppedItem;
import com.nukkitx.api.entity.system.System;
import com.nukkitx.api.entity.system.SystemRunner;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.util.BoundingBox;
import com.nukkitx.protocol.bedrock.packet.EntityEventPacket;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.level.NukkitLevel;

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
        ItemStack itemStack = containedItem.getItem();
        if (itemStack.isFull()) {
            return;
        }

        BoundingBox enlargedBB = entity.getBoundingBox().grow(1f);

        for (BaseEntity bbEntity : ((NukkitLevel) entity.getLevel()).getEntityManager().getEntitiesInBounds(enlargedBB)) {
            if (bbEntity == entity) {
                continue;
            }
            if (bbEntity instanceof DroppedItem) {
                ContainedItem containedItem1 = bbEntity.ensureAndGet(ContainedItem.class);
                if (itemStack.isMergeable(containedItem1.getItem())) {
                    int newAmount = itemStack.getAmount() + containedItem1.getItem().getAmount();

                    // Check if a merge is possible
                    if (newAmount > itemStack.getItemType().getMaximumStackSize()) {
                        continue;
                    }

                    containedItem.setItem(itemStack.toBuilder().amount(newAmount).build());

                    // Send merge to client
                    EntityEventPacket entityEvent = new EntityEventPacket();
                    entityEvent.setRuntimeEntityId(entity.getEntityId());
                    entityEvent.setData(newAmount);
                    entityEvent.setEvent(EntityEventPacket.Event.MERGE_ITEMS);

                    ((NukkitLevel) entity.getLevel()).getPacketManager().queuePacketForViewers(entity, entityEvent);
                    // Remove entity.
                    bbEntity.remove();
                    // Set blockPosition to midpoint
                    entity.setPositionFromSystem(entity.getPosition().add(bbEntity.getPosition()).div(2f).add(0,0.5f, 0));
                    break;
                }
            }
        }
    }
}
