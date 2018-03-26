package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;

import static cn.nukkit.server.nbt.util.VarInt.readUnsignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.readString;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeString;

public class PhotoTransferPacket implements MinecraftPacket {
    private String name;
    private byte[] data;
    private String bookId;

    @Override
    public void encode(ByteBuf buffer) {
        writeString(buffer, name);
        writeUnsignedInt(buffer, data.length);
        buffer.writeBytes(data);
        writeString(buffer, bookId);
    }

    @Override
    public void decode(ByteBuf buffer) {
        name = readString(buffer);
        data = new byte [readUnsignedInt(buffer)];
        buffer.readBytes(data);
        bookId = readString(buffer);
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
