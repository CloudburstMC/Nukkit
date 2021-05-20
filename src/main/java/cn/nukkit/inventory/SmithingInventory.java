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

package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author joserobjr
 * @since 2020-09-28
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class SmithingInventory extends FakeBlockUIComponent {
    private static final int EQUIPMENT = 0;
    private static final int INGREDIENT = 1;
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final int SMITHING_EQUIPMENT_UI_SLOT = 51;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final int SMITHING_INGREDIENT_UI_SLOT = 52;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public SmithingInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.SMITHING_TABLE, 51, position);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public SmithingRecipe matchRecipe() {
        return Server.getInstance().getCraftingManager().matchSmithingRecipe(getEquipment(), getIngredient());
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        if (index == EQUIPMENT || index == INGREDIENT) {
            updateResult();
        }
        super.onSlotChange(index, before, send);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void updateResult() {
        Item result;
        SmithingRecipe recipe = matchRecipe();
        if (recipe == null) {
            result =  Item.get(0);
        } else {
            result = recipe.getFinalResult(getEquipment());
        }
        setResult(result);
    }
    
    private void setResult(Item result) {
        //playerUI.setItem(50, result);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public Item getResult() {
        SmithingRecipe recipe = matchRecipe();
        if (recipe == null) {
            return Item.get(0);
        }
        return recipe.getFinalResult(getEquipment());
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Item getEquipment() {
        return getItem(EQUIPMENT);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setEquipment(Item equipment) {
        setItem(EQUIPMENT, equipment);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public Item getIngredient() {
        return getItem(INGREDIENT);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setIngredient(Item ingredient) {
        setItem(INGREDIENT, ingredient);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_SMITHING;
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);
        who.craftingType = Player.CRAFTING_SMALL;

        who.giveItem(getItem(EQUIPMENT), getItem(INGREDIENT));
        
        this.clear(EQUIPMENT);
        this.clear(INGREDIENT);
        playerUI.clear(50);
    }
}
