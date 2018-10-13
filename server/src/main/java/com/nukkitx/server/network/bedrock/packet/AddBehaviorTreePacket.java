package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class AddBehaviorTreePacket implements BedrockPacket {
    private String behaviorTreeJson;

    @Override
    public void encode(ByteBuf byteBuf) {

    }

    @Override
    public void decode(ByteBuf byteBuf) {

    }

    @Override
    public void handle(BedrockPacketHandler handler) {

    }
}
