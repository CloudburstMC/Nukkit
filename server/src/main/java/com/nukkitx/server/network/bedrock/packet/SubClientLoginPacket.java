package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.readLEAsciiString;
import static com.nukkitx.server.network.util.VarInts.readUnsignedInt;

@Data
public class SubClientLoginPacket implements BedrockPacket {
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
