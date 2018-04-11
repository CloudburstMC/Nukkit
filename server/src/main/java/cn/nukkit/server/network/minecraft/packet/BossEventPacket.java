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

import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeString;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeUniqueEntityId;

@Data
public class BossEventPacket implements MinecraftPacket {
    private long bossUniqueEntityId;
    private Type type;
    private long playerUniqueEntityId;
    private String title;
    private float healthPercentage;
    private short unknown0;
    private int color;
    private int overlay;

    @Override
    public void encode(ByteBuf buffer) {
        writeUniqueEntityId(buffer, bossUniqueEntityId);
        writeUnsignedInt(buffer, type.ordinal());

        switch (type) {
            case REGISTER_PLAYER:
            case UNREGISTER_PLAYER:
                writeUniqueEntityId(buffer, playerUniqueEntityId);
                break;
            case SHOW:
                writeString(buffer, title);
                buffer.writeFloatLE(healthPercentage);
            case UNKNOWN:
                buffer.writeShortLE(unknown0);
            case TEXTURE:
                writeUnsignedInt(buffer, color);
                writeUnsignedInt(buffer, overlay);
                break;
            case HEALTH_PERCENTAGE:
                buffer.writeFloatLE(healthPercentage);
                break;
            case TITLE:
                writeString(buffer, title);
                break;
            default:
                throw new RuntimeException("BossEvent type was unknown!");
        }
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // This packet isn't handled
    }

    public enum Type {
        /**
         * Shows the bossbar to the player.
         */
        SHOW,
        /**
         * Registers a player to a boss fight.
         */
        REGISTER_PLAYER,
        /**
         * Removes the bossbar from the client.
         */
        HIDE,
        /**
         * Unregisters a player from a boss fight.
         */
        UNREGISTER_PLAYER,
        /**
         * Appears not to be implemented. Currently bar percentage only appears to change in response to the target entity's health.
         */
        HEALTH_PERCENTAGE,
        /**
         * Also appears to not be implemented. Title clientside sticks as the target entity's nametag, or their entity type name if not set.
         */
        TITLE,
        /**
         * Not sure on this. Includes color and overlay fields, plus an unknown short. TODO: check this
         */
        UNKNOWN,
        /**
         * Not implemented :( Intended to alter bar appearance, but these currently produce no effect on clientside whatsoever.
         */
        TEXTURE
    }
}
