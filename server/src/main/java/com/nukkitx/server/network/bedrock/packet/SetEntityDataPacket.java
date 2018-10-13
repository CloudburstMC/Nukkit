package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import com.nukkitx.server.network.bedrock.util.MetadataDictionary;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeRuntimeEntityId;

@Data
public class SetEntityDataPacket implements BedrockPacket {
    private long runtimeEntityId;
    private final MetadataDictionary metadata = new MetadataDictionary();

    @Override
    public void encode(ByteBuf buffer) {
        writeRuntimeEntityId(buffer, runtimeEntityId);
        metadata.writeTo(buffer);
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
