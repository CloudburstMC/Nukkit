package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.minecraft.MinecraftUtil.writeString;

@Data
public class GuiDataPickItemPacket implements MinecraftPacket {
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
