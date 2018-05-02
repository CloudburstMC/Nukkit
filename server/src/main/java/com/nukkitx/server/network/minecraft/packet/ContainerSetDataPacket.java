package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.nbt.util.VarInt.writeSignedInt;

@Data
public class ContainerSetDataPacket implements MinecraftPacket {
    private byte inventoryId;
    private int property; //TODO: Add property type enum.
    private int value;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(inventoryId);
        writeSignedInt(buffer, property);
        writeSignedInt(buffer, value);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // This packet isn't handled
    }
}
