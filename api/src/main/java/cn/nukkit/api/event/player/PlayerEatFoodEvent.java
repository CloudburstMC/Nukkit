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

package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemInstance;

public class PlayerEatFoodEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final ItemInstance food;
    private float foodLevelAdded;
    private int saturationAdded;
    private boolean cancelled = false;

    public PlayerEatFoodEvent(final Player player, ItemInstance food, float foodLevelAdded, int saturationAdded) {
        this.player = player;
        this.food = food;
        this.foodLevelAdded = foodLevelAdded;
        this.saturationAdded = saturationAdded;
    }

    public ItemInstance getFood() {
        return food;
    }

    public float getFoodLevelAdded() {
        return foodLevelAdded;
    }

    public void setFoodLevelAdded(float foodLevelAdded) {
        this.foodLevelAdded = foodLevelAdded;
    }

    public int getSaturationAdded() {
        return saturationAdded;
    }

    public void setSaturationAdded(int staturationAdded) {
        this.saturationAdded = staturationAdded;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
