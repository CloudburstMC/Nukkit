package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.api.util.GameMode;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.util.VarInts.readInt;
import static com.nukkitx.server.network.util.VarInts.writeInt;

@Data
public class SetPlayerGameTypePacket implements BedrockPacket {
    private GameMode gamemode;

    @Override
    public void encode(ByteBuf buffer) {
        writeInt(buffer, gamemode.ordinal());
    }

    @Override
    public void decode(ByteBuf buffer) {
        gamemode = GameMode.parse(readInt(buffer));
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
