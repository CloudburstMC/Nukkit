package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.server.block.Block;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.item.Item;

/**
 * @author CreeperFace
 */
public class PlayerBlockPickEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Block blockClicked;
    private Item item;

    public PlayerBlockPickEvent(Player player, Block blockClicked, Item item) {
        this.blockClicked = blockClicked;
        this.item = item;
        this.player = player;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Block getBlockClicked() {
        return blockClicked;
    }
}
