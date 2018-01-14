package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.nbt.util.VarInt;
import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import com.flowpowered.math.vector.Vector3i;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
public class AddHangingEntityPacket implements MinecraftPacket {
    private long uniqueEntityId;
    private long runtimeEntityId;
    private Vector3i blockPosition;
    private int rotation;

    @Override
    public void encode(ByteBuf buffer) {
        writeUniqueEntityId(buffer, uniqueEntityId);
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writeBlockPosition(buffer, blockPosition);
        VarInt.writeSignedInt(buffer, rotation);
    }

    @Override
    public void decode(ByteBuf buffer) {
        uniqueEntityId = readUniqueEntityId(buffer);
        runtimeEntityId = readRuntimeEntityId(buffer);
        blockPosition = readBlockPosition(buffer);
        rotation = VarInt.readSignedInt(buffer);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // This packet isn't handled
    }
}
