package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.util.VarInts.readInt;
import static com.nukkitx.server.network.util.VarInts.writeInt;

@Data
public class RequestChunkRadiusPacket implements BedrockPacket {
    private int radius;

    @Override
    public void encode(ByteBuf buffer) {
        writeInt(buffer, radius);
    }

    @Override
    public void decode(ByteBuf buffer) {
        radius = readInt(buffer);
    }

    @Override
    public void handle(BedrockPacketHandler handler) {
        handler.handle(this);
    }
}
