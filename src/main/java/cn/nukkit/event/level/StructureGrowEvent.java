package cn.nukkit.event.level;

import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

import java.util.List;
import java.util.Objects;

/**
 * @author KCodeYT (Nukkit Project)
 */
@Since("1.3.2.0-PN")
public class StructureGrowEvent extends LevelEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Since("1.3.2.0-PN")
    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Block block;
    private final List<Block> blocks;

    @Since("1.3.2.0-PN")
    public StructureGrowEvent(Block block, List<Block> blocks) {
        super(Objects.requireNonNull(block.getLevel()));
        this.block = block;
        this.blocks = blocks;
    }

    @Since("1.3.2.0-PN")
    public Block getBlock() {
        return this.block;
    }

    @Since("1.3.2.0-PN")
    public List<Block> getBlockList() {
        return this.blocks;
    }

    @Since("1.3.2.0-PN")
    public void setBlockList(List<Block> blocks) {
        this.blocks.clear();
        if(blocks != null)
            this.blocks.addAll(blocks);
    }

}
