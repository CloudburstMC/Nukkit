package com.nukkitx.server.network.bedrock.packet;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.readVector3i;
import static com.nukkitx.server.network.bedrock.BedrockUtil.writeVector3i;

@Data
public class LabTablePacket implements BedrockPacket {
    private byte unknownByte0;
    private Vector3i blockEntityPosition;
    private byte reactionType;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(unknownByte0);
        writeVector3i(buffer, blockEntityPosition);
        buffer.writeByte(reactionType);
    }

    @Override
    public void decode(ByteBuf buffer) {
        unknownByte0 = buffer.readByte();
        blockEntityPosition = readVector3i(buffer);
        reactionType = buffer.readByte();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
