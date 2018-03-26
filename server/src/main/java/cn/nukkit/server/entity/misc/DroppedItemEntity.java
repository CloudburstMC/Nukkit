package cn.nukkit.server.entity.misc;

import cn.nukkit.api.entity.component.ContainedItem;
import cn.nukkit.api.entity.component.PickupDelay;
import cn.nukkit.api.entity.misc.DroppedItem;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.entity.BaseEntity;
import cn.nukkit.server.entity.EntityType;
import cn.nukkit.server.entity.component.ContainedItemComponent;
import cn.nukkit.server.entity.component.PickupDelayComponent;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.packet.AddItemEntityPacket;
import com.flowpowered.math.vector.Vector3f;

public class DroppedItemEntity extends BaseEntity implements DroppedItem {
    public DroppedItemEntity(Vector3f position, NukkitLevel level, NukkitServer server, ItemInstance itemDropped) {
        super(EntityType.ITEM, position, level, server);

        this.registerComponent(PickupDelay.class, new PickupDelayComponent(20));
        this.registerComponent(ContainedItem.class, new ContainedItemComponent(itemDropped));
    }

    @Override
    public MinecraftPacket createAddEntityPacket() {
        AddItemEntityPacket packet = new AddItemEntityPacket();
        packet.setItemInstance(ensureAndGet(ContainedItem.class).getItem());
        packet.setMotion(getMotion());
        packet.setPosition(getGamePosition());
        packet.setRuntimeEntityId(getEntityId());
        packet.setUniqueEntityId(getEntityId());
        packet.getMetadata().putAll(getMetadata());
        return packet;
    }
}
