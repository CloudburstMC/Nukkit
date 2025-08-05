package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.inventory.SmithingInventory;
import cn.nukkit.item.Item;
import lombok.Getter;

public class GrindstoneItemAction extends InventoryAction {

    @Getter
    private final int type;

    public GrindstoneItemAction(Item sourceItem, Item targetItem, int type) {
        super(sourceItem, targetItem);
        this.type = type;
    }

    @Override
    public boolean isValid(Player source) {
        return source.getWindowById(Player.GRINDSTONE_WINDOW_ID) instanceof SmithingInventory;
    }

    @Override
    public boolean execute(Player source) {
        return true;
    }

    @Override
    public void onExecuteSuccess(Player source) {
    }

    @Override
    public void onExecuteFail(Player source) {
    }
}
