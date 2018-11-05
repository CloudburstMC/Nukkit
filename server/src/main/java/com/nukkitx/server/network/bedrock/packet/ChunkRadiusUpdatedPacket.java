package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.util.VarInts.writeInt;

@Data
public class ChunkRadiusUpdatedPacket implements BedrockPacket {
    private int radius;

    @Override
    public void encode(ByteBuf buffer) {
        writeInt(buffer, radius);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(BedrockPacketHandler handler) {
        // This packet isn't handled
    }
}
