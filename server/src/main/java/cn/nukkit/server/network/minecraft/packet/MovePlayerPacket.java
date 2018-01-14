package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.util.Rotation;
import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import com.flowpowered.math.vector.Vector3f;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
public class MovePlayerPacket implements MinecraftPacket {
    private long runtimeEntityId;
    private Vector3f position;
    private Rotation rotation;
    private Mode mode;
    private boolean onGround;
    private long ridingRuntimeEntityId;
    private TeleportationCause teleportationCause;
    private int unknown0;

    @Override
    public void encode(ByteBuf buffer) {
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writeVector3f(buffer, position);
        writeRotation(buffer, rotation);
        buffer.writeByte(mode.ordinal());
        buffer.writeBoolean(onGround);
        writeRuntimeEntityId(buffer, ridingRuntimeEntityId);
        if (mode == Mode.TELEPORT) {
            buffer.writeIntLE(teleportationCause.ordinal());
            buffer.writeIntLE(unknown0);
        }
    }

    @Override
    public void decode(ByteBuf buffer) {
        runtimeEntityId = readRuntimeEntityId(buffer);
        position = readVector3f(buffer);
        rotation = readRotation(buffer);
        mode = Mode.values()[buffer.readByte()];
        onGround = buffer.readBoolean();
        ridingRuntimeEntityId = readRuntimeEntityId(buffer);
        if (mode == Mode.TELEPORT) {
            teleportationCause = TeleportationCause.values()[buffer.readIntLE()];
            unknown0 = buffer.readIntLE();
        }
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }

    public enum Mode {
        NORMAL,
        RESET,
        TELEPORT,
        PITCH
    }

    public enum TeleportationCause {
        UNKNOWN,
        PROJECTILE,
        CHORUS_FRUIT,
        COMMAND,
        BEHAVIOR,
        COUNT
    }
}
