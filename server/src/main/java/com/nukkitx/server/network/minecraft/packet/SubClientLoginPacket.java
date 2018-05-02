package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;
import lombok.Data;

import static com.nukkitx.nbt.util.VarInt.readUnsignedInt;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.readLEAsciiString;

@Data
public class SubClientLoginPacket implements MinecraftPacket {
    private AsciiString chainData;
    private AsciiString skinData;

    @Override
    public void encode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode(ByteBuf buffer) {
        ByteBuf jwt = buffer.readSlice(readUnsignedInt(buffer));
        chainData = readLEAsciiString(jwt);
        skinData = readLEAsciiString(jwt);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
