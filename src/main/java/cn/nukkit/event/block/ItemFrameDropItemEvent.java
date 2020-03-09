package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.ItemFrame;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;
import cn.nukkit.player.Player;

/**
 * Created by Pub4Game on 03.07.2016.
 */
public class ItemFrameDropItemEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Item item;
    private final ItemFrame itemFrame;

    public ItemFrameDropItemEvent(Player player, Block block, ItemFrame itemFrame, Item item) {
        super(block);
        this.player = player;
        this.itemFrame = itemFrame;
        this.item = item;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemFrame getItemFrame() {
        return itemFrame;
    }

    public Item getItem() {
        return item;
    }
}