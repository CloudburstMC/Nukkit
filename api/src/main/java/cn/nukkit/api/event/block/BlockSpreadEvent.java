package cn.nukkit.api.event.block;

import cn.nukkit.api.block.Block;
import cn.nukkit.api.event.Cancellable;

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
