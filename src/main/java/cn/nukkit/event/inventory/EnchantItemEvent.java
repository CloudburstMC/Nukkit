package cn.nukkit.event.inventory;

import cn.nukkit.Player;
import cn.nukkit.api.Since;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.item.Item;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Since("1.3.1.0-PN")
public class EnchantItemEvent extends InventoryEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Item oldItem;
    private Item newItem;
    private int xpCost;
    private Player enchanter;

    @Since("1.3.1.0-PN")
    public EnchantItemEvent(EnchantInventory inventory, Item oldItem, Item newItem, int cost, Player p) {
        super(inventory);
        this.oldItem = oldItem;
        this.newItem = newItem;
        this.xpCost = cost;
        this.enchanter = p;
    }
}
