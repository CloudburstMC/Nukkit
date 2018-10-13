package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.util.VarInts.readInt;
import static com.nukkitx.server.network.util.VarInts.writeUnsignedInt;

@Data
public class RiderJumpPacket implements BedrockPacket {
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
        unknown0 = readInt(buffer);
    }

    @Override
    public void handle(BedrockPacketHandler handler) {
        handler.handle(this);
    }
}
