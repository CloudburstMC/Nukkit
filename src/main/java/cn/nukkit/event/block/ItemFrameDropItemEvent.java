package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Pub4Game
 * @since 03.07.2016
 */
public class ItemFrameDropItemEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Item item;
    private final BlockEntityItemFrame itemFrame;

    public ItemFrameDropItemEvent(@Nullable Player player, @Nonnull Block block, @Nonnull BlockEntityItemFrame itemFrame, @Nonnull Item item) {
        super(block);
        this.player = player;
        this.itemFrame = itemFrame;
        this.item = item;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

    @Nonnull
    public BlockEntityItemFrame getItemFrame() {
        return itemFrame;
    }

    @Nonnull
    public Item getItem() {
        return item;
    }
}
