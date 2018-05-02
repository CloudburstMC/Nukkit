package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.api.util.GameMode;
import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.nbt.util.VarInt.writeSignedInt;

@Data
public class SetDefaultGameTypePacket implements MinecraftPacket {
    private GameMode gamemode;

    @Override
    public void encode(ByteBuf buffer) {
        writeSignedInt(buffer, gamemode.ordinal());
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Client bound only.
    }
}
