package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.annotations.NoEncryption;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeString;

@Data
@NoEncryption // This is sent in plain text to complete the Diffie Hellman key exchange.
public class ServerToClientHandshakePacket implements MinecraftPacket {
    private String jwt;

    @Override
    public void encode(ByteBuf buffer) {
        writeString(buffer, jwt);
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
