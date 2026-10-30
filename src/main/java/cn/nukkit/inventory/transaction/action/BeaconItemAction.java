package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.inventory.BeaconInventory;
import cn.nukkit.item.Item;

public class BeaconItemAction extends InventoryAction {

    public BeaconItemAction(Item sourceItem, Item targetItem) {
        super(sourceItem, targetItem);
    }

    @Override
    public boolean isValid(Player source) {
        return source.getWindowById(Player.BEACON_WINDOW_ID) instanceof BeaconInventory;
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
}
