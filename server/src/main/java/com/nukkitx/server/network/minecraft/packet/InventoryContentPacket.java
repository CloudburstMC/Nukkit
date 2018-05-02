package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.Arrays;

import static com.nukkitx.nbt.util.VarInt.readUnsignedInt;
import static com.nukkitx.nbt.util.VarInt.writeUnsignedInt;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.readItemInstance;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.writeItemInstance;

@Data
public class InventoryContentPacket implements MinecraftPacket {
    private int windowId;
    private ItemInstance[] items;

    @Override
    public void encode(ByteBuf buffer) {
        writeUnsignedInt(buffer, windowId);
        writeUnsignedInt(buffer, items.length);
        for (ItemInstance item : items) {
            writeItemInstance(buffer, item);
        }
    }

    @Override
    public void decode(ByteBuf buffer) {
        windowId = readUnsignedInt(buffer);
        int itemSize = readUnsignedInt(buffer);
        items = new ItemInstance[itemSize];
        for (int i = 0; i < itemSize; i++) {
            items[i] = readItemInstance(buffer);
        }
    }

    public void setItems(ItemInstance[] items) {
        this.items = Arrays.copyOf(items, items.length);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
