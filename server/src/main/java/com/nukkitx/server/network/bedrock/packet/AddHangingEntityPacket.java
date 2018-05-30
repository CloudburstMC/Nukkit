package com.nukkitx.server.network.bedrock.packet;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.*;
import static com.nukkitx.server.network.util.VarInts.readInt;
import static com.nukkitx.server.network.util.VarInts.writeInt;

@Data
public class AddHangingEntityPacket implements BedrockPacket {
    private long uniqueEntityId;
    private long runtimeEntityId;
    private Vector3i blockPosition;
    private int rotation;

    @Override
    public void encode(ByteBuf buffer) {
        writeUniqueEntityId(buffer, uniqueEntityId);
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writeVector3i(buffer, blockPosition);
        writeInt(buffer, rotation);
    }

    @Override
    public void decode(ByteBuf buffer) {
        uniqueEntityId = readUniqueEntityId(buffer);
        runtimeEntityId = readRuntimeEntityId(buffer);
        blockPosition = readVector3i(buffer);
        rotation = readInt(buffer);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // This packet isn't handled
    }
}
