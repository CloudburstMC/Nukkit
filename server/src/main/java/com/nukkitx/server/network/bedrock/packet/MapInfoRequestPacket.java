package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.readUniqueEntityId;
import static com.nukkitx.server.network.bedrock.BedrockUtil.writeUniqueEntityId;

@Data
public class MapInfoRequestPacket implements BedrockPacket {
    private long mapId;

    @Override
    public void encode(ByteBuf buffer) {
        writeUniqueEntityId(buffer, mapId);
    }

    @Override
    public void decode(ByteBuf buffer) {
        mapId = readUniqueEntityId(buffer);
    }

    @Override
    public void handle(BedrockPacketHandler handler) {
        handler.handle(this);
    }
}
