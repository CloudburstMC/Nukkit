package cn.nukkit.api.event.block;

import cn.nukkit.api.block.Block;
import cn.nukkit.api.event.Cancellable;

public class BlockBurnEvent implements BlockEvent, Cancellable {
    private final Block block;
    private boolean cancelled;

    public BlockBurnEvent(Block block) {
        this.block = block;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public Block getBlock() {
        return block;
    }
}
