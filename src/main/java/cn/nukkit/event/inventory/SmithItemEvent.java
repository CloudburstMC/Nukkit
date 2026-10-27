package cn.nukkit.event.inventory;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.SmithingInventory;
import cn.nukkit.item.Item;
import lombok.Getter;

public class SmithItemEvent extends InventoryEvent implements Cancellable {

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
    private final Item templateItem;
    @Getter
    private final Player player;

    public SmithItemEvent(SmithingInventory inventory, Item oldItem, Item newItem, Item materialItem, Item templateItem, Player player) {
        super(inventory);
        this.oldItem = oldItem;
        this.newItem = newItem;
        this.materialItem = materialItem;
        this.templateItem = templateItem;
        this.player = player;
    }
}
