package cn.nukkit.event.level;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

import java.util.List;
import java.util.Objects;

/**
 * @author KCodeYT (Nukkit Project)
 */
public class StructureGrowEvent extends LevelEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Block block;
    private final List<Block> blocks;

    public StructureGrowEvent(Block block, List<Block> blocks) {
        super(Objects.requireNonNull(block.getLevel()));
        this.block = block;
        this.blocks = blocks;
    }

    public Block getBlock() {
        return this.block;
    }

    public List<Block> getBlockList() {
        return this.blocks;
    }

    public void setBlockList(List<Block> blocks) {
        this.blocks.clear();
        if (blocks != null) {
            this.blocks.addAll(blocks);
        }
    }
}
