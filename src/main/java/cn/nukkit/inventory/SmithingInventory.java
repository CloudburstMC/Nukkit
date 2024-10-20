package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Position;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * @author joserobjr
 */
public class SmithingInventory extends FakeBlockUIComponent {

    private static final int EQUIPMENT = 0;
    private static final int INGREDIENT = 1;

    public static final int SMITHING_EQUIPMENT_UI_SLOT = 51;

    public static final int SMITHING_INGREDIENT_UI_SLOT = 52;

    private Item currentResult = Item.get(0);

    private static final IntSet ITEMS = new IntOpenHashSet(new int[]{
            Item.AIR, ItemID.NETHERITE_INGOT, ItemID.DIAMOND_SWORD, ItemID.DIAMOND_SHOVEL, ItemID.DIAMOND_PICKAXE, ItemID.DIAMOND_AXE,
            ItemID.DIAMOND_HOE, ItemID.DIAMOND_HELMET, ItemID.DIAMOND_CHESTPLATE, ItemID.DIAMOND_LEGGINGS, ItemID.DIAMOND_BOOTS,
            ItemID.NETHERITE_SWORD, ItemID.NETHERITE_SHOVEL, ItemID.NETHERITE_PICKAXE, ItemID.NETHERITE_AXE, ItemID.NETHERITE_HOE,
            ItemID.NETHERITE_HELMET, ItemID.NETHERITE_CHESTPLATE, ItemID.NETHERITE_LEGGINGS, ItemID.NETHERITE_BOOTS});

    public SmithingInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.SMITHING_TABLE, 51, position);
    }

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

    public void updateResult() {
        Item result;
        SmithingRecipe recipe = matchRecipe();
        if (recipe == null) {
            result = Item.get(0);
        } else {
            result = recipe.getFinalResult(getEquipment());
        }
        setResult(result);
    }

    private void setResult(Item result) {
        this.currentResult = result;
    }

    public Item getResult() {
        SmithingRecipe recipe = matchRecipe();
        if (recipe == null) {
            return Item.get(0);
        }
        return recipe.getFinalResult(getEquipment());
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

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_SMITHING;
    }

    public Item getCurrentResult() {
        return currentResult;
    }

    @Override
    public boolean allowedToAdd(Item item) {
        return ITEMS.contains(item.getId());
    }
}
