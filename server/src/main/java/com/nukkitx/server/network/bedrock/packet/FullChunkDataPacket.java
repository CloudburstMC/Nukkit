package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static com.nukkitx.server.network.util.VarInts.writeInt;
import static com.nukkitx.server.network.util.VarInts.writeUnsignedInt;

@Data
@ToString(exclude = {"data"})
@EqualsAndHashCode(exclude = {"data"})
public class FullChunkDataPacket implements BedrockPacket {
    private int chunkX;
    private int chunkZ;
    private byte[] data;

    @Override
    public void encode(ByteBuf buffer) {
        writeInt(buffer, chunkX);
        writeInt(buffer, chunkZ);
        writeUnsignedInt(buffer, data.length);
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
