package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockUtil;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class ScriptCustomEventPacket implements BedrockPacket {
    private String eventName;
    private String data;

    @Override
    public void encode(ByteBuf buffer) {
        BedrockUtil.writeString(buffer, eventName);
        BedrockUtil.writeString(buffer, data);
    }

    @Override
    public void decode(ByteBuf byteBuf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Client bound only
    }
}

