package cn.nukkit.server.entity.system;

import cn.nukkit.api.entity.Entity;
import cn.nukkit.api.entity.component.ContainedItem;
import cn.nukkit.api.entity.misc.DroppedItem;
import cn.nukkit.api.entity.system.System;
import cn.nukkit.api.entity.system.SystemRunner;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.util.BoundingBox;
import cn.nukkit.server.entity.BaseEntity;
import cn.nukkit.server.entity.EntityEvent;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.network.minecraft.packet.EntityEventPacket;

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
                }
            }
        }
    }
}
