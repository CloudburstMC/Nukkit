package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.LoomItemEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.LoomInventory;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.LoomItemAction;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

import java.util.List;

public class LoomTransaction extends InventoryTransaction {

    private Item outputItem;

    public LoomTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions);
    }

    @Override
    public void addAction(InventoryAction action) {
        super.addAction(action);

        if (action instanceof LoomItemAction) {
            outputItem = action.getSourceItem();
        }
    }

    @Override
    public boolean canExecute() {
        if (!super.canExecute()) {
            return false;
        }

        Inventory inventory = getSource().getWindowById(Player.LOOM_WINDOW_ID);
        if (!(inventory instanceof LoomInventory)) {
            return false;
        }

        if (outputItem == null) {
            return false;
        }

        LoomInventory loomInventory = (LoomInventory) inventory;
        Item banner = loomInventory.getBanner();
        Item dye = loomInventory.getDye();
        if (banner.getId() != Item.BANNER || dye.getId() != Item.DYE || banner.getDamage() != outputItem.getDamage()) {
            return false;
        }

        if (!outputItem.hasCompoundTag()) {
            return false;
        }

        int patternCount = outputItem.getNamedTag().getList("Patterns").size();
        if (banner.getNamedTag() == null) {
            return patternCount == 1;
        }

        if (patternCount > 6) {
            return false;
        }

        Item pattern = loomInventory.getPattern();
        if (pattern.getId() != 0 && pattern.getId() != ItemID.BANNER_PATTERN) {
            return false;
        }

        return banner.getNamedTag().getList("Patterns").size() + 1 == patternCount;
    }

    @Override
    protected boolean callExecuteEvent() {
        LoomInventory inventory = (LoomInventory) getSource().getWindowById(Player.LOOM_WINDOW_ID);
        LoomItemEvent event = new LoomItemEvent(inventory, this.outputItem, this.source);
        this.source.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            this.sendInventories();
            source.setNeedSendInventory(true);
            return false;
        }
        return true;
    }

    public Item getOutputItem() {
        return this.outputItem;
    }

    public static boolean isIn(List<InventoryAction> actions) {
        for (InventoryAction action : actions) {
            if (action instanceof LoomItemAction) return true;
        }
        return false;
    }

    @Override
    public boolean checkForItemPart(List<InventoryAction> actions) {
        return isIn(actions);
    }
}
