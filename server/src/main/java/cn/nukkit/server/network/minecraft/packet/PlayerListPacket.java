/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.util.Skin;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static cn.nukkit.server.nbt.util.VarInt.writeSignedInt;
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
                writeString(buffer, entry.getThirdPartyName());
                writeSignedInt(buffer, entry.getPlatformId());
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
