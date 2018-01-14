package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import com.flowpowered.math.vector.Vector3i;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.readBlockPosition;

@Data
public class BlockPickRequestPacket implements MinecraftPacket {
    private Vector3i blockPosition;
    private boolean addUserData;
    private byte selectedSlot;

    @Override
    public void encode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode(ByteBuf buffer) {
        blockPosition = readBlockPosition(buffer);
        addUserData = buffer.readBoolean();
        selectedSlot = buffer.readByte();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
