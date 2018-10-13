package com.nukkitx.server.network.bedrock.packet;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.util.Rotation;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import com.nukkitx.server.util.bitset.IntBitSet;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.*;

@Data
public class MoveEntityAbsolutePacket implements BedrockPacket {
    private static final int FLAG_TELEPORTED = 0x01;
    private static final int FLAG_ON_GROUND = 0x02;
    private long runtimeEntityId;
    private final IntBitSet flags = new IntBitSet();
    private Vector3f position;
    private Rotation rotation;

    @Override
    public void encode(ByteBuf buffer) {
        writeRuntimeEntityId(buffer, runtimeEntityId);
        buffer.writeByte(flags.get());
        writeVector3f(buffer, position);
        writeByteRotation(buffer, rotation);
    }

    @Override
    public void decode(ByteBuf buffer) {
        runtimeEntityId = readRuntimeEntityId(buffer);
        flags.set(buffer.readByte());
        position = readVector3f(buffer);
        rotation = readByteRotation(buffer);
    }

    @Override
    public void handle(BedrockPacketHandler handler) {
        handler.handle(this);
    }

    public void setOnGround(boolean onGround) {
        flags.set(FLAG_ON_GROUND, onGround);
    }

    public boolean isOnGround() {
        return flags.get(FLAG_ON_GROUND);
    }

    public void setTeleported(boolean teleported) {
        flags.set(FLAG_TELEPORTED, teleported);
    }

    public boolean isTeleported() {
        return flags.get(FLAG_TELEPORTED);
    }
}
