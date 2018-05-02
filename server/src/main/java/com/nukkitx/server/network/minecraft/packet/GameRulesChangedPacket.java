package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.api.level.GameRules;
import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.minecraft.MinecraftUtil.writeGameRules;

@Data
public class GameRulesChangedPacket implements MinecraftPacket {
    private GameRules gameRules;

    @Override
    public void encode(ByteBuf buffer) {
        writeGameRules(buffer, gameRules);
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
