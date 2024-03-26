package cn.nukkit.event.inventory;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.LoomInventory;
import cn.nukkit.item.Item;

public class LoomItemEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Item newItem;
    private final Player player;

    public LoomItemEvent(LoomInventory inventory, Item newItem, Player player) {
        super(inventory);
        this.newItem = newItem;
        this.player = player;
    }

    public Item getNewItem() {
        return this.newItem;
    }

    public Player getPlayer() {
        return this.player;
    }
}
