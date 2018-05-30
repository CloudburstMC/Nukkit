package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.api.util.GameMode;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.util.VarInts.writeInt;

@Data
public class SetDefaultGameTypePacket implements BedrockPacket {
    private GameMode gamemode;

    @Override
    public void encode(ByteBuf buffer) {
        writeInt(buffer, gamemode.ordinal());
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
