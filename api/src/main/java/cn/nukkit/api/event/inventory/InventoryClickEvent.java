package cn.nukkit.api.event.inventory;

import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.inventory.Inventory;
import cn.nukkit.server.item.Item;

/**
 * author: boybook
 * Nukkit Project
 */
public class InventoryClickEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
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

    public static HandlerList getHandlers() {
        return handlers;
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