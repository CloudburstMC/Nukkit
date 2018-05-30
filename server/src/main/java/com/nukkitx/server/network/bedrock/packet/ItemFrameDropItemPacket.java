package com.nukkitx.server.network.bedrock.packet;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.readVector3i;
import static com.nukkitx.server.network.bedrock.BedrockUtil.writeVector3i;

@Data
public class ItemFrameDropItemPacket implements BedrockPacket {
    private Vector3i blockPosition;

    @Override
    public void encode(ByteBuf buffer) {
        writeVector3i(buffer, blockPosition);
    }

    @Override
    public void decode(ByteBuf buffer) {
        blockPosition = readVector3i(buffer);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
