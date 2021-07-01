/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.nukkit.event.inventory;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.AnvilInventory;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Fired when a player change anything in the item name in an open Anvil inventory window.
 * 
 * @author joserobjr
 * @since 2021-02-14
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ToString
public class PlayerTypingAnvilInventoryEvent extends InventoryEvent {
    private static final HandlerList handlers = new HandlerList();
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Player player;
    private final String previousName;
    private final String typedName;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public PlayerTypingAnvilInventoryEvent(@Nonnull Player player, @Nonnull AnvilInventory inventory, @Nullable String previousName, @Nonnull String typedName) {
        super(inventory);
        this.player = player;
        this.previousName = previousName;
        this.typedName = typedName;
    }

    @Override
    @Nonnull
    public AnvilInventory getInventory() {
        return (AnvilInventory) super.getInventory();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Player getPlayer() {
        return player;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public String getTypedName() {
        return typedName;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public String getPreviousName() {
        return previousName;
    }
}
