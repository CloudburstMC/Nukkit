package cn.nukkit.event.inventory;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.Inventory;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class InventoryPickupItemEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final EntityItem item;
    private final Player player;
    private final Level level;
    
    public InventoryPickupItemEvent(Inventory inventory, EntityItem item, Player player, Level level) {
        super(inventory);
        this.item = item;
        this.player = player;
        this.level = level;
    }

    public EntityItem getItem() {
        return item;
    }
    
     public Player getPlayer() {
        return player;
    }
    
    public Level getLevel() {
        return level;
    }
    
}
