package com.nukkitx.server.network.bedrock.packet;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.api.util.Rotation;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.util.VarInts;
import com.nukkitx.server.util.bitset.IntBitSet;
import io.netty.buffer.ByteBuf;

public class MoveEntityDeltaPacket implements BedrockPacket {
    private static final int HAS_X = 0b1;
    private static final int HAS_Y = 0b10;
    private static final int HAS_Z = 0b100;
    private static final int HAS_PITCH = 0b1000;
    private static final int HAS_YAW = 0b10000;
    private static final int HAS_ROLL = 0b100000;

    private final IntBitSet flags = new IntBitSet();
    private Vector3i movementDelta;
    private Rotation rotationDelta;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(flags.get());
        writeInt(buffer, HAS_X, movementDelta.getX());
        writeInt(buffer, HAS_Y, movementDelta.getY());
        writeInt(buffer, HAS_Z, movementDelta.getZ());
        writeFloat(buffer, HAS_PITCH, rotationDelta.getPitch());
        writeFloat(buffer, HAS_YAW, rotationDelta.getHeadYaw());
        writeFloat(buffer, HAS_ROLL, rotationDelta.getYaw());
    }

    @Override
    public void decode(ByteBuf buffer) {
        flags.set(buffer.readByte());
        movementDelta = Vector3i.from(
                readInt(buffer, HAS_X),
                readInt(buffer, HAS_Y),
                readInt(buffer, HAS_Z)
        );
        rotationDelta = Rotation.from(
                readInt(buffer, HAS_PITCH),
                readInt(buffer, HAS_YAW),
                readInt(buffer, HAS_ROLL)
        );
    }

    private int readInt(ByteBuf buf, int flag) {
        if (flags.get(flag)) {
            return VarInts.readInt(buf);
        }
        return 0;
    }

    private float readFloat(ByteBuf buf, int flag) {
        if (flags.get(flag)) {
            return buf.readFloatLE();
        }
        return 0f;
    }

    private void writeInt(ByteBuf buf, int flag, int value) {
        boolean set = value == 0;
        flags.set(flag, set);
        if (set) {
            VarInts.writeInt(buf, value);
        }
    }

    private void writeFloat(ByteBuf buf, int flag, float value) {
        boolean set = value == 0f;
        flags.set(flag, set);
        if (set) {
            buf.writeFloatLE(value);
        }
    }
}
