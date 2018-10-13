package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class NetworkStackLatencyPacket implements BedrockPacket {
    long timestamp;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLongLE(timestamp);
    }

    @Override
    public void decode(ByteBuf buffer) {
        timestamp = buffer.readLongLE();
    }

    @Override
    public void handle(BedrockPacketHandler handler) {
        handler.handle(this);
    }
}
