package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class EnchantInventory extends FakeBlockUIComponent {

    public static final int ENCHANT_INPUT_ITEM_UI_SLOT = 14;
    public static final int ENCHANT_REAGENT_UI_SLOT = 15;

    public EnchantInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.ENCHANT_TABLE, 14, position);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.ENCHANT_WINDOW_ID;
    }

    public Item getInputSlot() {
        return this.getItem(0);
    }

    public Item getOutputSlot() {
        return this.getItem(0);
    }

    public Item getReagentSlot() {
        return this.getItem(1);
    }
}
