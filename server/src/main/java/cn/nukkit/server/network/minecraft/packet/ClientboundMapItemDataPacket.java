package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class ClientboundMapItemDataPacket implements MinecraftPacket {
    @Override
    public void encode(ByteBuf buffer) {
        // TODO: Rewrite
    }

    @Override
    public void decode(ByteBuf buffer) {

    }

    @Override
    public void handle(NetworkPacketHandler handler) {

    }
}
