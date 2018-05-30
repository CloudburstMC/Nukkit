package com.nukkitx.server.network.bedrock.packet;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeString;
import static com.nukkitx.server.network.bedrock.BedrockUtil.writeVector3i;

@Data
public class PlaySoundPacket implements BedrockPacket {
    private String sound;
    private Vector3i blockPosition;
    private float volume;
    private float pitch;

    @Override
    public void encode(ByteBuf buffer) {
        writeString(buffer, sound);
        writeVector3i(buffer, blockPosition);
        buffer.writeFloatLE(volume);
        buffer.writeFloatLE(pitch);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only client bound.
    }
}
