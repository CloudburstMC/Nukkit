package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.nbt.util.VarInt.readUnsignedInt;
import static com.nukkitx.nbt.util.VarInt.writeUnsignedInt;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.readItemInstance;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.writeItemInstance;

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
