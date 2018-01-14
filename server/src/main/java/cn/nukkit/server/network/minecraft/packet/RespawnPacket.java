package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import com.flowpowered.math.vector.Vector3f;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.readVector3f;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeVector3f;

@Data
public class RespawnPacket implements MinecraftPacket {
    private Vector3f position;

    @Override
    public void encode(ByteBuf buffer) {
        writeVector3f(buffer, position);
    }

    @Override
    public void decode(ByteBuf buffer) {
        position = readVector3f(buffer);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
