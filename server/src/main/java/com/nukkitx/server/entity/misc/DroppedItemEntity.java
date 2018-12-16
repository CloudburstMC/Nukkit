package com.nukkitx.server.entity.misc;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.entity.component.ContainedItem;
import com.nukkitx.api.entity.component.Physics;
import com.nukkitx.api.entity.component.PickupDelay;
import com.nukkitx.api.entity.misc.DroppedItem;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.protocol.bedrock.packet.AddItemEntityPacket;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.entity.BaseEntity;
import com.nukkitx.server.entity.EntityType;
import com.nukkitx.server.entity.component.ContainedItemComponent;
import com.nukkitx.server.entity.component.PhysicsComponent;
import com.nukkitx.server.entity.component.PickupDelayComponent;
import com.nukkitx.server.item.ItemUtil;
import com.nukkitx.server.level.NukkitLevel;

public class DroppedItemEntity extends BaseEntity implements DroppedItem {
    public DroppedItemEntity(Vector3f position, NukkitLevel level, NukkitServer server, ItemInstance itemDropped) {
        super(EntityType.ITEM, position, level, server);

        this.registerComponent(ContainedItem.class, new ContainedItemComponent(itemDropped));
        this.registerComponent(Physics.class, new PhysicsComponent(0.04f, 0.08f));
        this.registerComponent(PickupDelay.class, new PickupDelayComponent(20));
    }

    @Override
    public AddItemEntityPacket createAddEntityPacket() {
        AddItemEntityPacket packet = new AddItemEntityPacket();
        packet.setItemInstance(ItemUtil.toNetwork(ensureAndGet(ContainedItem.class).getItem()));
        packet.setMotion(getMotion());
        packet.setPosition(getGamePosition());
        packet.setRuntimeEntityId(getEntityId());
        packet.setUniqueEntityId(getEntityId());
        packet.getMetadata().putAll(getMetadata());
        return packet;
    }
}
