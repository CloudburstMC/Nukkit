package com.nukkitx.server.network.bedrock.packet;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeBlockPosition;
import static com.nukkitx.server.network.bedrock.BedrockUtil.writeUniqueEntityId;

@Data
public class ContainerOpenPacket implements BedrockPacket {
    private byte windowId;
    private byte type;
    private Vector3i blockPosition;
    private long uniqueEntityId;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(windowId);
        buffer.writeByte(type);
        writeBlockPosition(buffer, blockPosition);
        writeUniqueEntityId(buffer, uniqueEntityId);
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
