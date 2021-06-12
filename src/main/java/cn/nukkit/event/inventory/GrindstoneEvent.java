/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2021  José Roberto de Araújo Júnior
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
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.GrindstoneInventory;
import cn.nukkit.item.Item;

import javax.annotation.Nonnull;

/**
 * @author joserobjr
 * @since 2021-03-21
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class GrindstoneEvent extends InventoryEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static HandlerList getHandlers() {
        return handlers;
    }

    private final @Nonnull Item firstItem;
    private final @Nonnull Item resultItem;
    private final @Nonnull Item secondItem;
    private int experienceDropped;
    private final @Nonnull Player player;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public GrindstoneEvent(GrindstoneInventory inventory, @Nonnull Item firstItem, @Nonnull Item resultItem, @Nonnull Item secondItem, int cost, @Nonnull Player player) {
        super(inventory);
        this.firstItem = firstItem;
        this.resultItem = resultItem;
        this.secondItem = secondItem;
        this.experienceDropped = cost;
        this.player = player;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Item getFirstItem() {
        return this.firstItem;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Item getResultItem() {
        return this.resultItem;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Item getSecondItem() {
        return this.secondItem;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getExperienceDropped() {
        return this.experienceDropped;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setExperienceDropped(int experienceDropped) {
        this.experienceDropped = experienceDropped;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Player getPlayer() {
        return this.player;
    }
}
