package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import com.flowpowered.math.vector.Vector3i;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedLong;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeVector3i;

@Data
public class UpdateBlockSyncedPacket implements MinecraftPacket {
    private Vector3i blockPosition;
    private int runtimeId;
    private int unknownInt0;
    private int unknownInt1;
    private long unknownLong0;
    private long unknownLong1;

    @Override
    public void encode(ByteBuf buffer) {
        writeVector3i(buffer, blockPosition);
        writeUnsignedInt(buffer, runtimeId);
        writeUnsignedInt(buffer, unknownInt0);
        writeUnsignedInt(buffer, unknownInt1);
        writeUnsignedLong(buffer, unknownLong0);
        writeUnsignedLong(buffer, unknownLong1);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Client bound only
    }
}
