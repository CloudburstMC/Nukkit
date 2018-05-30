package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.UUID;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeString;
import static com.nukkitx.server.network.util.VarInts.writeUnsignedInt;

@Data
public class ResourcePackDataInfoPacket implements BedrockPacket {
    private UUID packId;
    private int maxChunkSize;
    private int chunkCount;
    private int compressedPackSize;
    private byte[] hash;

    @Override
    public void encode(ByteBuf buffer) {
        writeString(buffer, packId.toString());
        buffer.writeIntLE(maxChunkSize);
        buffer.writeIntLE(chunkCount);
        buffer.writeLongLE(compressedPackSize);
        writeUnsignedInt(buffer, hash.length);
        buffer.writeBytes(hash);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only clientbound
    }
}
