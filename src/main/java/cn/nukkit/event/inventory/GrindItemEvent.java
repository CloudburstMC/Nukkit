package cn.nukkit.event.inventory;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.GrindstoneInventory;
import cn.nukkit.item.Item;
import lombok.Getter;

public class GrindItemEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    @Getter
    private final Item oldItem;
    @Getter
    private final Item newItem;
    @Getter
    private final Item materialItem;
    @Getter
    private final Player player;

    public GrindItemEvent(GrindstoneInventory inventory, Item oldItem, Item newItem, Item materialItem, Player player) {
        super(inventory);
        this.oldItem = oldItem;
        this.newItem = newItem;
        this.materialItem = materialItem;
        this.player = player;
    }
}
