package cn.nukkit.inventory.transaction.action;

import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.item.Item;
import cn.nukkit.player.Player;
import cn.nukkit.utils.MainLogger;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * @author CreeperFace
 */
@ToString(callSuper = true)
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
     *
     * @return inventory
     */
    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Returns the inventorySlot in the inventory which this action modified.
     *
     * @return slot
     */
    public int getSlot() {
        return inventorySlot;
    }

    /**
     * Checks if the item in the inventory at the specified inventorySlot is the same as this action's source item.
     *
     * @param source player
     * @return valid
     */
    public boolean isValid(Player source) {
        Item check = inventory.getItem(this.inventorySlot);

        boolean result = check.equalsExact(this.sourceItem);
        if (!result) {
            MainLogger.getLogger().debug("INVALID | Client: " + sourceItem + ", Server: " + check);
        }
        return result;
    }

    /**
     * Sets the item into the target inventory.
     *
     * @param source player
     * @return successfully executed
     */
    public boolean execute(Player source) {
        return this.inventory.setItem(this.inventorySlot, this.targetItem, false);
    }

    /**
     * Sends inventorySlot changes to other viewers of the inventory. This will not send any change back to the source Player.
     *
     * @param source player
     */
    public void onExecuteSuccess(Player source) {
        Set<Player> viewers = new HashSet<>(this.inventory.getViewers());
        viewers.remove(source);

        this.inventory.sendSlot(this.inventorySlot, viewers);
    }

    /**
     * Sends the original inventorySlot contents to the source player to revert the action.
     *
     * @param source player
     */
    public void onExecuteFail(Player source) {
        this.inventory.sendSlot(this.inventorySlot, source);
    }

    @Override
    public void onAddToTransaction(InventoryTransaction transaction) {
        transaction.addInventory(this.inventory);
    }
}
