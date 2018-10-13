package com.nukkitx.server.network.bedrock.packet;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeBlockPosition;
import static com.nukkitx.server.network.util.VarInts.writeUnsignedInt;

@Data
public class NetworkChunkPublisherUpdatePacket implements BedrockPacket {
    private Vector3i position;
    private int radius;

    @Override
    public void encode(ByteBuf buffer) {
        writeBlockPosition(buffer, position);
        writeUnsignedInt(buffer, radius);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }
}
