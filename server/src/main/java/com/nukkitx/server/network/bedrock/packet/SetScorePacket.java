package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import com.nukkitx.server.network.bedrock.data.ScoreInfo;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeScoreInfo;

@Data
public class SetScorePacket implements BedrockPacket {
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
        SET,
        REMOVE
    }
}
