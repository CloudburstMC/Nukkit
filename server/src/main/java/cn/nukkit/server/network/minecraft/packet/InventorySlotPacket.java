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

package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.nbt.util.VarInt.readUnsignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.readItemInstance;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeItemInstance;

@Data
public class InventorySlotPacket implements MinecraftPacket {
    private int windowId;
    private int inventorySlot;
    private ItemInstance slot;

    @Override
    public void encode(ByteBuf buffer) {
        writeUnsignedInt(buffer, windowId);
        writeUnsignedInt(buffer, inventorySlot);
        writeItemInstance(buffer, slot);
    }

    @Override
    public void decode(ByteBuf buffer) {
        windowId = readUnsignedInt(buffer);
        inventorySlot = readUnsignedInt(buffer);
        slot = readItemInstance(buffer);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
