package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import com.nukkitx.server.network.minecraft.data.ScoreInfo;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.nukkitx.server.network.minecraft.MinecraftUtil.writeScoreInfo;

@Data
public class SetScorePacket implements MinecraftPacket {
    private Action action;
    private List<ScoreInfo> info = new ArrayList<>();

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(action.ordinal());
        writeScoreInfo(buffer, info);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Client bound only
    }

    public enum Action {
        MODIFY,
        RESET
    }
}
