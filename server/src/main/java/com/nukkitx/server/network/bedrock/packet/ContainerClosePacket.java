package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class ContainerClosePacket implements BedrockPacket {
    private byte windowId;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(windowId);
    }

    @Override
    public void decode(ByteBuf buffer) {
        windowId = buffer.readByte();
    }

    @Override
    public void handle(BedrockPacketHandler handler) {
        handler.handle(this);
    }
}
