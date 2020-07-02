package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;


/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantInventory extends FakeBlockUIComponent {

    @Since("1.3.1.0-PN") public static final int ENCHANT_INPUT_ITEM_UI_SLOT = 14;
    @Since("1.3.1.0-PN") public static final int ENCHANT_REAGENT_UI_SLOT = 15;

    public EnchantInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.ENCHANT_TABLE, 14, position);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_ENCHANT;
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);
        who.craftingType = Player.CRAFTING_SMALL;
        Item[] drops = new Item[]{ getItem(0), getItem(1) };
        drops = who.getInventory().addItem(drops);
        for (Item drop : drops) {
            if (!who.dropItem(drop)) {
                this.getHolder().getLevel().dropItem(this.getHolder().add(0.5, 0.5, 0.5), drop);
            }
        }

        clear(0);
        clear(1);
        who.resetCraftingGridType();
    }

    @Since("1.3.1.0-PN")
    public Item getInputSlot() {
        return this.getItem(0);
    }

    @Since("1.3.1.0-PN")
    public Item getOutputSlot() {
        return this.getItem(0);
    }

    @Since("1.3.1.0-PN")
    public Item getReagentSlot() {
        return this.getItem(1);
    }
}
