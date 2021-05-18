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

package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;

/**
 * @author joserobjr
 * @since 2021-05-16
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class SmithingItemAction extends InventoryAction {

    private final int type;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public SmithingItemAction(Item sourceItem, Item targetItem, int type) {
        super(sourceItem, targetItem);
        this.type = type;
    }

    @Override
    public boolean isValid(Player source) {
        return source.getWindowById(Player.SMITHING_WINDOW_ID) != null;
    }

    @Override
    public boolean execute(Player source) {
        return true;
    }

    @Override
    public void onExecuteSuccess(Player source) {
        // Does nothing
    }

    @Override
    public void onExecuteFail(Player source) {
        // Does nothing
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getType() {
        return type;
    }
}
