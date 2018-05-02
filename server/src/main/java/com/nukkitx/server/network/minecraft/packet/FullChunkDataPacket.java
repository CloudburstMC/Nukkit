package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static com.nukkitx.nbt.util.VarInt.writeSignedInt;
import static com.nukkitx.nbt.util.VarInt.writeUnsignedInt;

@Data
@ToString(exclude = {"data"})
@EqualsAndHashCode(exclude = {"data"})
public class FullChunkDataPacket implements MinecraftPacket {
    private int chunkX;
    private int chunkZ;
    private byte[] data;

    @Override
    public void encode(ByteBuf buffer) {
        writeSignedInt(buffer, chunkX);
        writeSignedInt(buffer, chunkZ);
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
