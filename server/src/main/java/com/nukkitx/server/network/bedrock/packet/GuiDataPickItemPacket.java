package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeString;

@Data
public class GuiDataPickItemPacket implements BedrockPacket {
    private String locale;
    private String popupMessage;
    private int hotbarSlot;

    @Override
    public void encode(ByteBuf buffer) {
        writeString(buffer, locale);
        writeString(buffer, popupMessage);
        buffer.writeIntLE(hotbarSlot);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {

    }
}
