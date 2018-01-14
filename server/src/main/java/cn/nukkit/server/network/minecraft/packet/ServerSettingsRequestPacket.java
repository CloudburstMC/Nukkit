package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class ServerSettingsRequestPacket implements MinecraftPacket {
    @Override
    public void encode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode(ByteBuf buffer) {
        // No payload
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
