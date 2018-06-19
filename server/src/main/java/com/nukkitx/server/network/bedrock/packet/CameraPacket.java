package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeUniqueEntityId;

@Data
public class CameraPacket implements BedrockPacket {
    private long cameraUniqueEntityId;
    private long playerUniqueEntityId;

    @Override
    public void encode(ByteBuf buffer) {
        writeUniqueEntityId(buffer, cameraUniqueEntityId);
        writeUniqueEntityId(buffer, playerUniqueEntityId);
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
