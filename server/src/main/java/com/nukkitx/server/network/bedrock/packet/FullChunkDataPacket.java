package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
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
    private ByteBuf data;

    @Override
    public void encode(ByteBuf buffer) {
        writeInt(buffer, chunkX);
        writeInt(buffer, chunkZ);
        writeUnsignedInt(buffer, data.readableBytes());
        buffer.writeBytes(data);
        data.release();
        data = null;
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(BedrockPacketHandler handler) {
        // Only client bound.
    }
}
