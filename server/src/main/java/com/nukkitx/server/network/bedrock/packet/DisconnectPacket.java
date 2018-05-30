package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeString;

@Data
public class DisconnectPacket implements BedrockPacket {
    private boolean disconnectScreenHidden;
    private String kickMessage;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeBoolean(disconnectScreenHidden);
        if (!disconnectScreenHidden) {
            writeString(buffer, kickMessage);
        }
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // This packet isn't handled
    }
}
