package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class SetEntityLinkPacket implements BedrockPacket {
    //private EntityLink entityLink TODO: Implement

    @Override
    public void encode(ByteBuf buffer) {

    }

    @Override
    public void decode(ByteBuf buffer) {

    }

    @Override
    public void handle(BedrockPacketHandler handler) {

    }
}
