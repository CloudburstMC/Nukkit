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

package cn.nukkit.server.inventory;

public enum WindowId {
    NONE(-1),
    INVENTORY(0),
    FIRST(1),
    LAST(100),
    OFFHAND(119),
    ARMOR(120),
    CREATIVE(121),
    HOTBAR(122),
    FIXED_INVENTORY(123),
    CURSOR(124);

    private final byte id;

    WindowId(int id) {
        this.id = (byte) id;
    }

    public byte id() {
        return id;
    }
}
