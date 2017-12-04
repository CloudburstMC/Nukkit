package cn.nukkit.server.inventory.transaction.action;

import cn.nukkit.server.Player;
import cn.nukkit.server.inventory.Inventory;
import cn.nukkit.server.item.Item;

import java.util.HashSet;
import java.util.Set;

/**
 * @author CreeperFace
 */
public class SlotChangeAction extends InventoryAction {

    protected Inventory inventory;
    private int inventorySlot;

    public SlotChangeAction(Inventory inventory, int inventorySlot, Item sourceItem, Item targetItem) {
        super(sourceItem, targetItem);
        this.inventory = inventory;
        this.inventorySlot = inventorySlot;
    }

    /**
     * Returns the inventory involved in this action.
     */
    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Returns the inventorySlot in the inventory which this action modified.
     */
    public int getSlot() {
        return inventorySlot;
    }

    /**
     * Checks if the item in the inventory at the specified inventorySlot is the same as this action's source item.
     */
    public boolean isValid(Player source) {
        Item check = inventory.getItem(this.inventorySlot);

        return check.equalsExact(this.sourceItem);
    }

    /**
     * Sets the item into the target inventory.
     */
    public boolean execute(Player source) {
        return this.inventory.setItem(this.inventorySlot, this.targetItem, false);
    }

    /**
     * Sends inventorySlot changes to other viewers of the inventory. This will not send any change back to the source Player.
     */
    public void onExecuteSuccess(Player source) {
        Set<Player> viewers = new HashSet<>(this.inventory.getViewers());
        viewers.remove(source);

        this.inventory.sendSlot(this.inventorySlot, viewers);
    }

    /**
     * Sends the original inventorySlot contents to the source player to revert the action.
     */
    public void onExecuteFail(Player source) {
        this.inventory.sendSlot(this.inventorySlot, source);
    }
}
