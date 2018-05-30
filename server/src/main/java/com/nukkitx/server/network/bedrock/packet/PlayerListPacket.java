package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.api.util.Skin;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.nukkitx.server.network.bedrock.BedrockUtil.*;
import static com.nukkitx.server.network.util.VarInts.writeInt;
import static com.nukkitx.server.network.util.VarInts.writeUnsignedInt;

@Data
public class PlayerListPacket implements BedrockPacket {
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
                writeString(buffer, entry.getThirdPartyName());
                writeInt(buffer, entry.getPlatformId());
                writeSkin(buffer, entry.getSkin());
                writeString(buffer, entry.getXuid());
                writeString(buffer, entry.getPlatformChatId());
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
    @ToString(exclude = {"entityId", "name", "thirdPartyName", "platformId", "skin", "xuid", "platformChatId"})
    @EqualsAndHashCode(exclude = {"skin"})
    public final static class Entry {
        private UUID uuid;
        private long entityId;
        private String name;
        private String thirdPartyName;
        private int platformId;
        private Skin skin;
        private String xuid;
        private String platformChatId;
    }
}
