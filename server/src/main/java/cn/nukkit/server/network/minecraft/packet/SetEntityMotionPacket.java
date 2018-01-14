package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import com.flowpowered.math.vector.Vector3f;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
public class SetEntityMotionPacket implements MinecraftPacket {
    private long runtimeEntityId;
    private Vector3f motion;

    @Override
    public void encode(ByteBuf buffer) {
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writeVector3f(buffer, motion);
    }

    @Override
    public void decode(ByteBuf buffer) {
        runtimeEntityId = readRuntimeEntityId(buffer);
        motion = readVector3f(buffer);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
