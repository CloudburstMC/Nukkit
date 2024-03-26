package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.HandlerList;

/**
 * Event for Block piston change.
 * @author CreeperFace on 2.8.2017.
 */
public class BlockPistonChangeEvent extends BlockEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final int oldPower;
    private final int newPower;

    /**
     * This event is called on piston activation/deactivation/change.
     * @param block Block (Piston) that is affected.
     * @param oldPower Old power (charge) of piston.
     * @param newPower New charge (updated) of piston.
     */
    public BlockPistonChangeEvent(Block block, int oldPower, int newPower) {
        super(block);
        this.oldPower = oldPower;
        this.newPower = newPower;
    }

    public int getOldPower() {
        return oldPower;
    }

    public int getNewPower() {
        return newPower;
    }
}
