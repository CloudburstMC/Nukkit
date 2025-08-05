package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.*;
import cn.nukkit.level.Position;

import java.util.Arrays;

/**
 * @author joserobjr
 */
public class SmithingInventory extends FakeBlockUIComponent {

    private static final int EQUIPMENT = 0;
    private static final int INGREDIENT = 1;
    private static final int TEMPLATE = 2;

    public static final int SMITHING_EQUIPMENT_UI_SLOT = 51;
    public static final int SMITHING_INGREDIENT_UI_SLOT = 52;
    public static final int SMITHING_TEMPLATE_UI_SLOT = 53;

    public SmithingInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.SMITHING_TABLE, 51, position);
    }

    public SmithingRecipe matchRecipe() {
        return Server.getInstance().getCraftingManager().matchSmithingRecipe(Arrays.asList(getEquipment(), getIngredient(), getTemplate()));
    }

    public Item getResult() {
        Item trimOutput = this.getTrimOutputItem();
        if (trimOutput != null) {
            return trimOutput;
        }

        SmithingRecipe recipe = matchRecipe();
        if (recipe == null) {
            return Item.get(0);
        }

        return recipe.getFinalResult(getEquipment(), getTemplate());
    }

    private Item getTrimOutputItem() {
        Item ingredient = getIngredient();
        Item template = getTemplate();

        if (ingredient instanceof ItemTrimMaterial && template instanceof ItemTrimPattern) {
            Item input = getEquipment();

            if (input instanceof ItemArmor) {
                return ((ItemArmor) input).setArmorTrim(((ItemTrimPattern) template).getPattern(), ((ItemTrimMaterial) ingredient).getMaterial());
            }
        }

        return null;
    }

    public Item getEquipment() {
        return getItem(EQUIPMENT);
    }

    public void setEquipment(Item equipment) {
        setItem(EQUIPMENT, equipment);
    }

    public Item getIngredient() {
        return getItem(INGREDIENT);
    }

    public void setIngredient(Item ingredient) {
        setItem(INGREDIENT, ingredient);
    }

    public Item getTemplate() {
        return getItem(TEMPLATE);
    }

    public void setTemplate(Item template) {
        setItem(TEMPLATE, template);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.SMITHING_WINDOW_ID;
    }
}
