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

package cn.nukkit.server.network.minecraft.data;

import lombok.Value;

import java.util.OptionalLong;
import java.util.UUID;

@Value
public class CommandOriginData {
    private Origin origin;
    private UUID uuid;
    private String requestId;
    private long unknown0;

    public OptionalLong getUnknown0() {
        return (unknown0 == -1) ? OptionalLong.empty() : OptionalLong.of(unknown0);
    }

    public enum Origin {
        PLAYER,
        COMMAND_BLOCK,
        MINECART_COMMAND_BLOCK,
        DEV_CONSOLE,
        TEST,
        AUTOMATION_PLAYER,
        CLIENT_AUTOMATION,
        DEDICATED_SERVER,
        ENTITY,
        VIRTUAL,
        GAME_ARGUMENT,
        INTERNAL
    }
}
