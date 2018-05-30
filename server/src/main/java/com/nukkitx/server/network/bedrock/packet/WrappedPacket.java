package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.network.raknet.CustomRakNetPacket;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.session.BedrockSession;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WrappedPacket implements CustomRakNetPacket<BedrockSession> {
    private final List<BedrockPacket> packets = new ArrayList<>();
    private boolean encrypted;
    private ByteBuf payload;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeBytes(payload);
    }

    @Override
    public void decode(ByteBuf buffer) {
        payload = buffer.readBytes(buffer.readableBytes());
    }

    @Override
    public void handle(BedrockSession session) throws Exception {
        session.onWrappedPacket(this);
    }
}
