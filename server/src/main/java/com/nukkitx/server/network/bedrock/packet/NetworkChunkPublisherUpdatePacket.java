package com.nukkitx.server.network.bedrock.packet;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeVector3i;
import static com.nukkitx.server.network.util.VarInts.writeUnsignedInt;

@Data
public class NetworkChunkPublisherUpdatePacket implements BedrockPacket {
    private Vector3i position;
    private int unknown0; // radius?

    @Override
    public void encode(ByteBuf buffer) {
        writeVector3i(buffer, position);
        writeUnsignedInt(buffer, unknown0);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }
}
