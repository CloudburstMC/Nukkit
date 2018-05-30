package com.nukkitx.server.network.bedrock.packet;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeVector3i;
import static com.nukkitx.server.network.util.VarInts.writeInt;

@Data
public class BlockEventPacket implements BedrockPacket {
    private Vector3i blockPosition;
    private Type eventType;
    private int eventData;

    @Override
    public void encode(ByteBuf buffer) {
        writeVector3i(buffer, blockPosition);
        writeInt(buffer, eventType.ordinal());
        writeInt(buffer, eventData);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // This packet isn't handled
    }

    public enum Type {
        NONE,
        CHEST
    }
}
