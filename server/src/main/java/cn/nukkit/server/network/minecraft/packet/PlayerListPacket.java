package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.util.Skin;
import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.*;

@Data
public class PlayerListPacket implements MinecraftPacket {
    private final List<Entry> entries = new ArrayList<>();
    private Type type;

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(type.ordinal());
        writeUnsignedInt(buffer, entries.size());

        for (Entry entry: entries) {
            writeUuid(buffer, entry.getUuid());

            if (type == Type.ADD) {
                writeUniqueEntityId(buffer, entry.getEntityId());
                writeString(buffer, entry.getName());
                writeSkin(buffer, entry.getSkin());
                writeString(buffer, entry.getXuid());
            }
        }
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only client bound.
    }

    public enum Type {
        ADD,
        REMOVE
    }

    @Value
    @ToString(exclude = {"xuid", "entityId", "name", "skin"})
    @EqualsAndHashCode(exclude = {"skin"})
    public final static class Entry {
        private final UUID uuid;
        private String xuid;
        private long entityId;
        private String name;
        private Skin skin;
    }
}
