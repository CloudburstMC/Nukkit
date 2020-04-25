package cn.nukkit.event.inventory;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;

/**
 * @author CreeperFace
 * <p>
 * Called when inventory transaction is not caused by a player
 */
public class InventoryMoveItemEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Inventory targetInventory;
    private final InventoryHolder source;
    private final Action action;
    private Item item;

    public InventoryMoveItemEvent(Inventory from, Inventory targetInventory, InventoryHolder source, Item item, Action action) {
        super(from);
        this.targetInventory = targetInventory;
        this.source = source;
        this.item = item;
        this.action = action;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Inventory getTargetInventory() {
        return targetInventory;
    }

    public InventoryHolder getSource() {
        return source;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Action getAction() {
        return action;
    }

    public enum Action {
        SLOT_CHANGE, //transaction between 2 inventories
        PICKUP,
        DROP,
        DISPENSE
    }
}
