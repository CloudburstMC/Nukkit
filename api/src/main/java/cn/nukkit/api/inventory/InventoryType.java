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

package cn.nukkit.api.inventory;

/**
 * Inventory types that are supported
 */
public enum InventoryType {
    /**
     * Single chest
     */
    CHEST(27),

    /**
     * Ender chest
     */
    ENDER_CHEST(27),

    /**
     * Double chest equivalent to two chests
     */
    DOUBLE_CHEST(27 + 27),

    /**
     * Player inventory
     */
    PLAYER(36),

    /**
     * Furnace inventory
     */
    FURNACE(3),

    /**
     * 2 x 2 crafting grid inventory
     */
    CRAFTING(5),

    /**
     * Crafting table inventory
     */
    WORKBENCH(10),

    /**
     * Brewing stand inventory
     */
    BREWING_STAND(5),

    /**
     * Anvil inventory
     */
    ANVIL(3),

    /**
     * Enchantment table inventory
     */
    ENCHANTMENT_TABLE(2),

    /**
     * Dispenser inventory
     */
    DISPENSER(9),

    /**
     * Dropper inventory
     */
    DROPPER(9),

    /**
     * Hopper inventory
     */
    HOPPER(5);

    private final byte size;

    InventoryType(int size) {
        this.size = (byte) size;
    }

    public byte getSize() {
        return size;
    }
}
