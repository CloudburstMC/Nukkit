package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.inventory.SmithingInventory;
import cn.nukkit.item.Item;

/**
 * @author joserobjr
 */
public class SmithingItemAction extends InventoryAction {

    private final int type;

    public SmithingItemAction(Item sourceItem, Item targetItem, int type) {
        super(sourceItem, targetItem);
        this.type = type;
    }

    @Override
    public boolean isValid(Player source) {
        return source.getWindowById(Player.SMITHING_WINDOW_ID) instanceof SmithingInventory;
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

    public int getType() {
        return type;
    }
}
