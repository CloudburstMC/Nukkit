package com.nukkitx.server.network.bedrock.packet;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.util.Rotation;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.*;

@Data
public class MoveEntityPacket implements BedrockPacket {
    private long runtimeEntityId;
    private Vector3f position;
    private Rotation rotation;
    private boolean onGround;
    private boolean teleported;

    @Override
    public void encode(ByteBuf buffer) {
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writeVector3f(buffer, position);
        writeByteRotation(buffer, rotation);
        buffer.writeBoolean(onGround);
        buffer.writeBoolean(teleported);
    }

    @Override
    public void decode(ByteBuf buffer) {
        runtimeEntityId = readRuntimeEntityId(buffer);
        position = readVector3f(buffer);
        rotation = readByteRotation(buffer);
        onGround = buffer.readBoolean();
        teleported = buffer.readBoolean();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
