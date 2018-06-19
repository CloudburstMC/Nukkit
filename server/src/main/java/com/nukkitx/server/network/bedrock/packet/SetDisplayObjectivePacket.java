package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.writeString;
import static com.nukkitx.server.network.util.VarInts.writeInt;

@Data
public class SetDisplayObjectivePacket implements BedrockPacket {
    private DisplaySlot displaySlot;
    private String objectiveId;
    private String displayName;
    private Criteria criteria;
    private int sortOrder;

    @Override
    public void encode(ByteBuf buffer) {
        writeString(buffer, displaySlot.name().toLowerCase());
        writeString(buffer, objectiveId);
        writeString(buffer, displayName);
        writeString(buffer, criteria.name().toLowerCase());
        writeInt(buffer, sortOrder);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Client bound only
    }

    public enum Criteria {
        DUMMY
    }

    public enum DisplaySlot {
        LIST,
        SIDEBAR,
        BELOWNAME
    }
}
