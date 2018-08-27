package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.api.scoreboard.DisplayObjective;
import com.nukkitx.api.scoreboard.Objective;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeString;
import static com.nukkitx.server.network.util.VarInts.writeInt;

@Data
public class SetDisplayObjectivePacket implements BedrockPacket {
    private DisplayObjective displayObjective;

    @Override
    public void encode(ByteBuf buffer) {
        Objective objective = displayObjective.getObjective();

        writeString(buffer, displayObjective.getDisplaySlot().name().toLowerCase());
        writeString(buffer, objective.getName());
        writeString(buffer, objective.getDisplayName());
        writeString(buffer, objective.getCriteria().getName());
        writeInt(buffer, displayObjective.getSortOrder().ordinal());
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Client bound only
    }
}
