package cn.nukkit.event.inventory;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;

/**
 * author: boybook
 * Nukkit Project
 */
public class InventoryClickEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final int slot;
    private final Item sourceItem;
    private final Item heldItem;
    private final Item leftItem;

    public InventoryClickEvent(Inventory inventory, int slot, Item sourceItem, Item heldItem, Item leftItem) {
        super(inventory);
        this.slot = slot;
        this.sourceItem = sourceItem;
        this.heldItem = heldItem;
        this.leftItem = leftItem;
    }

    public int getSlot() {
        return slot;
    }

    public Item getSourceItem() {
        return sourceItem;
    }

    public Item getHeldItem() {
        return heldItem;
    }

    public Item getLeftItem() {
        return leftItem;
    }
}