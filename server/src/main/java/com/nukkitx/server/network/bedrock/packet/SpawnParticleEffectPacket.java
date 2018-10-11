package com.nukkitx.server.network.bedrock.packet;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeString;
import static com.nukkitx.server.network.bedrock.BedrockUtil.writeVector3f;

@Data
public class SpawnParticleEffectPacket implements BedrockPacket {
    private int dimensionId;
    private Vector3f position;
    private String identifier;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeShort(dimensionId);
        writeVector3f(buffer, position);
        writeString(buffer, identifier);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }
}
