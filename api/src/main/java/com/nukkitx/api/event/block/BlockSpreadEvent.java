package com.nukkitx.api.event.block;

import com.nukkitx.api.block.Block;
import com.nukkitx.api.event.Cancellable;

public class BlockSpreadEvent extends BlockFormEvent implements Cancellable {
    private final Block source;

    public BlockSpreadEvent(Block block, Block source, Block newState) {
        super(block, newState);
        this.source = source;
    }

    public Block getSource() {
        return source;
    }
}
