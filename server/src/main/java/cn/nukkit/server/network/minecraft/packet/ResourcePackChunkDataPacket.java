package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.UUID;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeString;

@Data
public class ResourcePackChunkDataPacket implements MinecraftPacket {
    private UUID packId;
    private int chunkIndex;
    private long progress;
    private byte[] data;

    @Override
    public void encode(ByteBuf buffer) {
        writeString(buffer, packId.toString());
        buffer.writeIntLE(chunkIndex);
        buffer.writeLongLE(progress);
        buffer.writeIntLE(data.length);
        buffer.writeBytes(data);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only client bound.
    }
}
