package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.raknet.NetworkPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * This isn't an actual Minecraft packet. It is used to batch together multiple packets, compress and encrypt them for
 * easier transmission.
 */
@Data
public class WrappedPacket implements NetworkPacket {
    private final List<MinecraftPacket> packets = new ArrayList<>();
    private ByteBuf payload;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeBytes(payload);
    }

    @Override
    public void decode(ByteBuf buffer) {
        payload = buffer.readBytes(buffer.readableBytes());
    }
}
