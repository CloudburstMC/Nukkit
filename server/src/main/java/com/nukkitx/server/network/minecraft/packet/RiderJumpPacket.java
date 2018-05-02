package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.nbt.util.VarInt.readSignedInt;
import static com.nukkitx.nbt.util.VarInt.writeUnsignedInt;

@Data
public class RiderJumpPacket implements MinecraftPacket {
    private int unknown0;
    /*
    Possible the jump boost bar?
    If the value is > 0. Set it to 0
    If the value is =< 90. Set it to 106535321 (wtf?)
     */

    @Override
    public void encode(ByteBuf buffer) {
        writeUnsignedInt(buffer, unknown0);
    }

    @Override
    public void decode(ByteBuf buffer) {
        unknown0 = readSignedInt(buffer);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
