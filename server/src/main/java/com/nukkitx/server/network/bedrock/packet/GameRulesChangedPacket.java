package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.api.level.GameRules;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeGameRules;

@Data
public class GameRulesChangedPacket implements BedrockPacket {
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
