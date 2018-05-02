package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.nbt.util.VarInt.writeSignedInt;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.writeString;

@Data
public class SetTitlePacket implements MinecraftPacket {
    private Type type;
    private String text;
    private int fadeInTime;
    private int stayTime;
    private int fadeOutTime;

    @Override
    public void encode(ByteBuf buffer) {
        writeSignedInt(buffer, type.ordinal());
        writeString(buffer, text);
        writeSignedInt(buffer, fadeInTime);
        writeSignedInt(buffer, stayTime);
        writeSignedInt(buffer, fadeOutTime);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only client bound.
    }

    public enum Type {
        CLEAR_TITLE,
        RESET_TITLE,
        SET_TITLE,
        SET_SUBTITLE,
        SET_ACTIONBAR_MESSAGE,
        SET_ANIMATION_TIMES
    }
}
