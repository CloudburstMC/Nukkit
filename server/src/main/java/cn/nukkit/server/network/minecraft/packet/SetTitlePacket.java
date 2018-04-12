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
public class SetTitlePacket implements MinecraftPacket {
    private Type type;
    private String text;
    private int fadeInTime;
    private int stayTime;
    private int fadeOutTime;

    @Override
    public void encode(ByteBuf buffer) {
        writeSignedInt(buffer, type.ordinal());
        writeString(buffer, text);
        writeSignedInt(buffer, fadeInTime);
        writeSignedInt(buffer, stayTime);
        writeSignedInt(buffer, fadeOutTime);
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
        CLEAR_TITLE,
        RESET_TITLE,
        SET_TITLE,
        SET_SUBTITLE,
        SET_ACTIONBAR_MESSAGE,
        SET_ANIMATION_TIMES
    }
}
