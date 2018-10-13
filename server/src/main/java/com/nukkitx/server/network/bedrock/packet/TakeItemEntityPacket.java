package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeRuntimeEntityId;

@Data
public class TakeItemEntityPacket implements BedrockPacket {
    private long itemRuntimeEntityId;
    private long runtimeEntityId;

    @Override
    public void encode(ByteBuf buffer) {
        writeRuntimeEntityId(buffer, itemRuntimeEntityId);
        writeRuntimeEntityId(buffer, runtimeEntityId);
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
