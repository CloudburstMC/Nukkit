package cn.nukkit.event.player;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;
import cn.nukkit.player.Player;

/**
 * Created by CreeperFace on 18.3.2017.
 */
public class PlayerMapInfoRequestEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private Item item;

    public PlayerMapInfoRequestEvent(Player player, Item item) {
        super(player);
        this.item = item;
    }

    public Item getMap() {
        return item;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
