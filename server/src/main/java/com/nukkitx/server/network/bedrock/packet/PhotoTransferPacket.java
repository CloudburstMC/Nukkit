package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import io.netty.buffer.ByteBuf;

import static com.nukkitx.server.network.bedrock.BedrockUtil.readString;
import static com.nukkitx.server.network.bedrock.BedrockUtil.writeString;
import static com.nukkitx.server.network.util.VarInts.readUnsignedInt;
import static com.nukkitx.server.network.util.VarInts.writeUnsignedInt;

public class PhotoTransferPacket implements BedrockPacket {
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
    public void handle(BedrockPacketHandler handler) {
        handler.handle(this);
    }
}
