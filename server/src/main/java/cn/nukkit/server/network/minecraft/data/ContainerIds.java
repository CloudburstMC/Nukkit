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

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ContainerIds {
    public static final int NONE = -1;
    public static final int INVENTORY = 0;
    public static final int FIRST = 1;
    public static final int LAST = 100;
    public static final int OFFHAND = 119;
    public static final int ARMOR = 120;
    public static final int CREATIVE = 121;
    public static final int HOTBAR = 122;
    public static final int FIXED_INVENTORY = 123;
    public static final int CURSOR = 124;
}
