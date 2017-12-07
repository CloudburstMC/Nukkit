package cn.nukkit.server.inventory.transaction.action;

import cn.nukkit.server.Player;
import cn.nukkit.server.item.Item;

/**
 * @author CreeperFace
 */
public abstract class InventoryAction {


    private long creationTime;

    protected Item sourceItem;

    protected Item targetItem;

    public InventoryAction(Item sourceItem, Item targetItem) {
        this.sourceItem = sourceItem;
        this.targetItem = targetItem;

        this.creationTime = System.currentTimeMillis();
    }

    public long getCreationTime() {
        return creationTime;
    }

    /**
     * Returns the item that was present before the action took place.
     *
     * @return ItemUse
     */
    public Item getSourceItem() {
        return sourceItem.clone();
    }

    /**
     * Returns the item that the action attempted to replace the source item with.
     */
    public Item getTargetItem() {
        return targetItem.clone();
    }

    /**
     * Called by inventory transactions before any actions are processed. If this returns false, the transaction will
     * be cancelled.
     */
    public boolean onPreExecute(Player source) {
        return true;
    }

    /**
     * Returns whether this action is currently valid. This should perform any necessary sanity checks.
     */
    abstract public boolean isValid(Player source);

    /**
     * Performs actions needed to complete the inventory-action server-side. Returns if it was successful. Will return
     * false if plugins cancelled events. This will only be called if the transaction which it is part of is considered
     * valid.
     */
    abstract public boolean execute(Player source);

    /**
     * Performs additional actions when this inventory-action completed successfully.
     */
    abstract public void onExecuteSuccess(Player $source);

    /**
     * Performs additional actions when this inventory-action did not complete successfully.
     */
    abstract public void onExecuteFail(Player source);
}
