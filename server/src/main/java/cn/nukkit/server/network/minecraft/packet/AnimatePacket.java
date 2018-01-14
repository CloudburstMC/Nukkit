package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.nbt.util.VarInt.readSignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeSignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.readRuntimeEntityId;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeRuntimeEntityId;

@Data
public class AnimatePacket implements MinecraftPacket {
    float unknown0;
    private Action action;
    private long runtimeEntityId;

    @Override
    public void encode(ByteBuf buffer) {
        writeSignedInt(buffer, action.id);
        writeRuntimeEntityId(buffer, runtimeEntityId);
        if (action == Action.ROW_RIGHT || action == Action.ROW_LEFT) {
            buffer.writeFloatLE(unknown0);
        }
    }

    @Override
    public void decode(ByteBuf buffer) {
        action = Action.values()[readSignedInt(buffer)];
        runtimeEntityId = readRuntimeEntityId(buffer);
        if (action == Action.ROW_RIGHT || action == Action.ROW_LEFT) {
            unknown0 = buffer.readFloatLE();
        }
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }

    public enum Action {
        NONE,
        SWING_ARM,
        WAKE_UP,
        CRITICAL_HIT,
        MAGIC_CRITICAL_HIT,
        ROW_RIGHT(128),
        ROW_LEFT(129);

        public final int id;

        Action() {
            id = ordinal();
        }

        Action(int id) {
            this.id = id;
        }
    }
}
