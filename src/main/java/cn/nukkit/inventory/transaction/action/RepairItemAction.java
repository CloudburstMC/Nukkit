package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;

@Since("1.4.0.0-PN")
public class RepairItemAction extends InventoryAction {

    private int type;

    @Since("1.4.0.0-PN")
    public RepairItemAction(Item sourceItem, Item targetItem, int type) {
        super(sourceItem, targetItem);
        this.type = type;
    }

    @Override
    public boolean isValid(Player source) {
        return source.getWindowById(Player.ANVIL_WINDOW_ID) != null;
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

    @Since("1.4.0.0-PN")
    public int getType() {
        return this.type;
    }
}
