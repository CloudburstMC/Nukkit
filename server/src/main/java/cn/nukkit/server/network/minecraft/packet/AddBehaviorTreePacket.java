package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.readString;

@Data
public class AddBehaviorTreePacket implements MinecraftPacket {
    private String unknown0;

    @Override
    public void encode(ByteBuf buffer) {
        unknown0 = readString(buffer);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // This packet isn't handled
    }
}
