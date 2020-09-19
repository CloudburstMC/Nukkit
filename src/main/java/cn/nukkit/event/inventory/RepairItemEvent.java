package cn.nukkit.event.inventory;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.item.Item;

public class RepairItemEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Item oldItem;
    private Item newItem;
    private Item materialItem;
    private int cost;
    private Player player;

    public RepairItemEvent(AnvilInventory inventory, Item oldItem, Item newItem, Item materialItem, int cost, Player player) {
        super(inventory);
        this.oldItem = oldItem;
        this.newItem = newItem;
        this.materialItem = materialItem;
        this.cost = cost;
        this.player = player;
    }

    public Item getOldItem() {
        return this.oldItem;
    }

    public Item getNewItem() {
        return this.newItem;
    }

    public Item getMaterialItem() {
        return this.materialItem;
    }

    public int getCost() {
        return this.cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Player getPlayer() {
        return this.player;
    }
}
