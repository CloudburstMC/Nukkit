package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import com.flowpowered.math.vector.Vector3f;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.nbt.util.VarInt.writeSignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeVector3f;

@Data
public class ChangeDimensionPacket implements MinecraftPacket {
    private int dimension;
    private Vector3f position;
    private boolean respawn;

    @Override
    public void encode(ByteBuf buffer) {
        writeSignedInt(buffer, dimension);
        writeVector3f(buffer, position);
        buffer.writeBoolean(respawn);
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
