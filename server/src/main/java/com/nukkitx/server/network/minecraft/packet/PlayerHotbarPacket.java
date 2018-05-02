package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.nukkitx.nbt.util.VarInt.readUnsignedInt;
import static com.nukkitx.nbt.util.VarInt.writeUnsignedInt;

@Data
public class PlayerHotbarPacket implements MinecraftPacket {
    private final List<ItemInstance> items = new ArrayList<>();
    private int selectedHotbarSlot;
    private byte windowId;
    private boolean selectHotbarSlot;

    @Override
    public void encode(ByteBuf buffer) {
        writeUnsignedInt(buffer, selectedHotbarSlot);
        buffer.writeByte(windowId);
    }

    @Override
    public void decode(ByteBuf buffer) {
        selectedHotbarSlot = readUnsignedInt(buffer);
        windowId = buffer.readByte();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
