package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.nbt.util.VarInt.readSignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeSignedInt;

@Data
public class RequestChunkRadiusPacket implements MinecraftPacket {
    private int radius;

    @Override
    public void encode(ByteBuf buffer) {
        writeSignedInt(buffer, radius);
    }

    @Override
    public void decode(ByteBuf buffer) {
        radius = readSignedInt(buffer);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
