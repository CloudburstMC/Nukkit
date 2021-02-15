package cn.nukkit.event.inventory;

import cn.nukkit.Player;
import cn.nukkit.api.Since;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.item.Item;

@Since("1.3.2.0-PN")
public class RepairItemEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Since("1.3.2.0-PN")
    public static HandlerList getHandlers() {
        return handlers;
    }

    private Item oldItem;
    private Item newItem;
    private Item materialItem;
    private int cost;
    private Player player;

    @Since("1.3.2.0-PN")
    public RepairItemEvent(AnvilInventory inventory, Item oldItem, Item newItem, Item materialItem, int cost, Player player) {
        super(inventory);
        this.oldItem = oldItem;
        this.newItem = newItem;
        this.materialItem = materialItem;
        this.cost = cost;
        this.player = player;
    }

    @Since("1.3.2.0-PN")
    public Item getOldItem() {
        return this.oldItem;
    }

    @Since("1.3.2.0-PN")
    public Item getNewItem() {
        return this.newItem;
    }

    @Since("1.3.2.0-PN")
    public Item getMaterialItem() {
        return this.materialItem;
    }

    @Since("1.3.2.0-PN")
    public int getCost() {
        return this.cost;
    }

    @Since("1.3.2.0-PN")
    public void setCost(int cost) {
        this.cost = cost;
    }

    @Since("1.3.2.0-PN")
    public Player getPlayer() {
        return this.player;
    }
}
