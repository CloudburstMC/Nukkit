package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.readRuntimeEntityId;

@Data
public class EntityFallPacket implements MinecraftPacket {
    private long runtimeEntityId;
    private float fallDistance;
    private boolean unknown0;

    @Override
    public void encode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode(ByteBuf buffer) {
        runtimeEntityId = readRuntimeEntityId(buffer);
        fallDistance = buffer.readFloatLE();
        unknown0 = buffer.readBoolean();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
