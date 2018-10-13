package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.UUID;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeString;

@Data
public class ResourcePackChunkDataPacket implements BedrockPacket {
    private UUID packId;
    private int chunkIndex;
    private long progress;
    private byte[] data;

    @Override
    public void encode(ByteBuf buffer) {
        writeString(buffer, packId.toString());
        buffer.writeIntLE(chunkIndex);
        buffer.writeLongLE(progress);
        buffer.writeIntLE(data.length);
        buffer.writeBytes(data);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(BedrockPacketHandler handler) {
        // Only client bound.
    }
}
