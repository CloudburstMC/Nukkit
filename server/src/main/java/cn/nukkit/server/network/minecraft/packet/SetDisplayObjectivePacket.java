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
