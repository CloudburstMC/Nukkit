package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeRuntimeEntityId;
import static com.nukkitx.server.network.util.VarInts.writeInt;

@Data
public class ShowCreditsPacket implements BedrockPacket {
    private long runtimeEntityId;
    private Status status;

    @Override
    public void encode(ByteBuf buffer) {
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writeInt(buffer, status.ordinal());
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(BedrockPacketHandler handler) {
        // Only client bound.
    }

    public enum Status {
        START_CREDITS,
        END_CREDITS
    }
}
