package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.nbt.util.VarInt.writeSignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeString;

@Data
public class SetDisplayObjectivePacket implements MinecraftPacket {
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
        writeSignedInt(buffer, sortOrder);
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
