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
