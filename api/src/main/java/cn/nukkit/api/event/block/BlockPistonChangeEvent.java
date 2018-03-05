package cn.nukkit.api.event.block;

import cn.nukkit.api.block.Block;

public class BlockPistonChangeEvent implements BlockEvent {
    private final Block block;
    private int oldPower;
    private int newPower;
    public BlockPistonChangeEvent(Block block, int oldPower, int newPower) {
        this.block = block;
        this.oldPower = oldPower;
        this.newPower = newPower;
    }

    public int getOldPower() {
        return oldPower;
    }

    public int getNewPower() {
        return newPower;
    }

    @Override
    public Block getBlock() {
        return block;
    }
}
