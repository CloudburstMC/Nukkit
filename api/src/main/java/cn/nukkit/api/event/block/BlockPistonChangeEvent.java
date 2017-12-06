package cn.nukkit.api.event.block;

import cn.nukkit.server.block.Block;
import cn.nukkit.server.event.HandlerList;

/**
 * Created by CreeperFace on 2.8.2017.
 */
public class BlockPistonChangeEvent extends BlockEvent {

    private static final HandlerList handlers = new HandlerList();
    private int oldPower;
    private int newPower;
    public BlockPistonChangeEvent(Block block, int oldPower, int newPower) {
        super(block);
        this.oldPower = oldPower;
        this.newPower = newPower;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public int getOldPower() {
        return oldPower;
    }

    public int getNewPower() {
        return newPower;
    }
}
