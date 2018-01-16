package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.nbt.util.VarInt;
import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import com.flowpowered.math.vector.Vector3i;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
public class AddPaintingPacket implements MinecraftPacket {
    private long uniqueEntityId;
    private long runtimeEntityId;
    private Vector3i blockPosition;
    private int direction;
    private String title;

    @Override
    public void encode(ByteBuf buffer) {
        writeUniqueEntityId(buffer, uniqueEntityId);
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writeVector3i(buffer, blockPosition);
        VarInt.writeSignedInt(buffer, direction);
        writeString(buffer, title);
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
