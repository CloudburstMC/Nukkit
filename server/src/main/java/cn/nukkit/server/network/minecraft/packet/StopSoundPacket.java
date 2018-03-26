package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeString;

@Data
public class StopSoundPacket implements MinecraftPacket {
    private String soundName;
    private boolean stoppingAllSound;

    @Override
    public void encode(ByteBuf buffer) {
        writeString(buffer, soundName);
        buffer.writeBoolean(stoppingAllSound);
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
