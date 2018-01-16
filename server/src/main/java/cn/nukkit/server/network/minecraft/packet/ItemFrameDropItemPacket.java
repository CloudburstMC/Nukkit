package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import com.flowpowered.math.vector.Vector3i;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.readVector3i;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeVector3i;

@Data
public class ItemFrameDropItemPacket implements MinecraftPacket {
    private Vector3i blockPosition;

    @Override
    public void encode(ByteBuf buffer) {
        writeVector3i(buffer, blockPosition);
    }

    @Override
    public void decode(ByteBuf buffer) {
        blockPosition = readVector3i(buffer);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
