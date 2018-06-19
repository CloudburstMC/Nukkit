package com.nukkitx.server.network.bedrock.packet;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeVector3f;
import static com.nukkitx.server.network.util.VarInts.writeInt;

@Data
public class SpawnExperienceOrbPacket implements BedrockPacket {
    private Vector3f position;
    private int amount;

    @Override
    public void encode(ByteBuf buffer) {
        writeVector3f(buffer, position);
        writeInt(buffer, amount);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only client bound
    }
}
