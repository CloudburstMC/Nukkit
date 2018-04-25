package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeRuntimeEntityId;

@Data
public class TakeItemEntityPacket implements MinecraftPacket {
    private long itemRuntimeEntityId;
    private long runtimeEntityId;

    @Override
    public void encode(ByteBuf buffer) {
        writeRuntimeEntityId(buffer, itemRuntimeEntityId);
        writeRuntimeEntityId(buffer, runtimeEntityId);
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
