package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class AnvilInventory extends FakeBlockUIComponent {

    public static final int ANVIL_INPUT_UI_SLOT = 1;
    public static final int ANVIL_MATERIAL_UI_SLOT = 2;
    public static final int ANVIL_OUTPUT_UI_SLOT = CREATED_ITEM_OUTPUT_UI_SLOT;

    public static final int TARGET = 0;
    public static final int SACRIFICE = 1;
    public static final int RESULT = ANVIL_OUTPUT_UI_SLOT - 1; //1: offset

    private int cost;

    public AnvilInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.ANVIL, 1, position);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.ANVIL_WINDOW_ID;
    }

    public Item getInputSlot() {
        return this.getItem(TARGET);
    }

    public Item getMaterialSlot() {
        return this.getItem(SACRIFICE);
    }

    public Item getOutputSlot() {
        return this.getItem(RESULT);
    }

    public int getCost() {
        return this.cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
