package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.nbt.util.VarInt.writeUnsignedInt;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.writeString;

@Data
public class ServerSettingsResponsePacket implements MinecraftPacket {
    private int formId;
    private String formData;

    @Override
    public void encode(ByteBuf buffer) {
        writeUnsignedInt(buffer, formId);
        writeString(buffer, formData);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only client bound.
    }
}
