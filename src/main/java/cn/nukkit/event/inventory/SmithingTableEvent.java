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
import cn.nukkit.inventory.SmithingInventory;
import cn.nukkit.item.Item;

import javax.annotation.Nonnull;

/**
 * @author joserobjr
 * @since 2021-05-16
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class SmithingTableEvent extends InventoryEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static HandlerList getHandlers() {
        return handlers;
    }

    private final @Nonnull Item equipmentItem;
    private final @Nonnull Item resultItem;
    private final @Nonnull Item ingredientItem;
    private final @Nonnull Player player;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public SmithingTableEvent(SmithingInventory inventory, @Nonnull Item equipmentItem, @Nonnull Item resultItem, @Nonnull Item ingredientItem, @Nonnull Player player) {
        super(inventory);
        this.equipmentItem = equipmentItem;
        this.resultItem = resultItem;
        this.ingredientItem = ingredientItem;
        this.player = player;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Item getEquipmentItem() {
        return this.equipmentItem;
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
    public Item getIngredientItem() {
        return this.ingredientItem;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Player getPlayer() {
        return this.player;
    }
}
