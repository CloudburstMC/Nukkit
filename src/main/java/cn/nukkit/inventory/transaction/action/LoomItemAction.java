package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.inventory.LoomInventory;
import cn.nukkit.item.Item;

public class LoomItemAction extends InventoryAction {

    private final LoomInventory inventory;

    public LoomItemAction(Item sourceItem, Item targetItem, LoomInventory inventory) {
        super(sourceItem, targetItem);
        this.inventory = inventory;
    }

    @Override
    public boolean isValid(Player source) {
        return source.getWindowById(Player.LOOM_WINDOW_ID) instanceof LoomInventory;
    }

    @Override
    public boolean execute(Player source) {
        return true;
    }

    @Override
    public void onExecuteSuccess(Player source) {
        Item banner = inventory.getBanner();
        Item dye = inventory.getDye();
        if (banner != null && !banner.isNull()) {
            banner.count -= sourceItem.getCount();
            inventory.setBanner(banner);
        }
        if (dye != null && !dye.isNull()) {
            dye.count -= sourceItem.getCount();
            inventory.setDye(dye);
        }
        // Pattern not consumed
    }

    @Override
    public void onExecuteFail(Player source) {
        inventory.sendContents(source);
    }
}
