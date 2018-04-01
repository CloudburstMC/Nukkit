package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import com.flowpowered.math.vector.Vector3i;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.readVector3i;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeVector3i;

@Data
public class LabTablePacket implements MinecraftPacket {
    private byte unknownByte0;
    private Vector3i blockEntityPosition;
    private byte reactionType;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(unknownByte0);
        writeVector3i(buffer, blockEntityPosition);
        buffer.writeByte(reactionType);
    }

    @Override
    public void decode(ByteBuf buffer) {
        unknownByte0 = buffer.readByte();
        blockEntityPosition = readVector3i(buffer);
        reactionType = buffer.readByte();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
