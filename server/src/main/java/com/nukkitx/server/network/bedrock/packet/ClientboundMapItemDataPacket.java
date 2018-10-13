package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class ClientboundMapItemDataPacket implements BedrockPacket {
    @Override
    public void encode(ByteBuf buffer) {
        // TODO: Rewrite
    }

    @Override
    public void decode(ByteBuf buffer) {

    }

    @Override
    public void handle(BedrockPacketHandler handler) {
        // This packet isn't handled
    }
}
