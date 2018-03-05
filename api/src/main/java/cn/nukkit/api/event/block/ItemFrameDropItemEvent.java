package cn.nukkit.api.event.block;

import cn.nukkit.api.Player;
import cn.nukkit.api.block.Block;
import cn.nukkit.api.block.entity.ItemFrameBlockEntity;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.item.ItemInstance;

public class ItemFrameDropItemEvent implements BlockEvent, Cancellable {
    private final Block block;
    private final Player player;
    private final ItemInstance item;
    private final ItemFrameBlockEntity itemFrame;
    private boolean cancellable;

    public ItemFrameDropItemEvent(Player player, Block block, ItemFrameBlockEntity itemFrame, ItemInstance item) {
        this.block = block;
        this.player = player;
        this.item = item;
        this.itemFrame = itemFrame;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemFrameBlockEntity getItemFrame() {
        return itemFrame;
    }

    public ItemInstance getItem() {
        return item;
    }

    @Override
    public boolean isCancelled() {
        return cancellable;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancellable = cancelled;
    }

    @Override
    public Block getBlock() {
        return block;
    }
}