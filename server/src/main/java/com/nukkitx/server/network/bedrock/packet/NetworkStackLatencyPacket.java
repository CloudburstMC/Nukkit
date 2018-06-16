package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.util.VarInts.readUnsignedLong;
import static com.nukkitx.server.network.util.VarInts.writeUnsignedLong;

@Data
public class NetworkStackLatencyPacket implements BedrockPacket {
    long timestamp;

    @Override
    public void encode(ByteBuf buffer) {
        readUnsignedLong(buffer);
    }

    @Override
    public void decode(ByteBuf buffer) {
        writeUnsignedLong(buffer, timestamp);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
