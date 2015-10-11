package cn.nukkit.event.inventory;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.block.BlockEvent;
import cn.nukkit.item.Item;
import cn.nukkit.tile.Furnace;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FurnaceBurnEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Furnace furnace;
    private Item fuel;
    private short burnTime;
    private boolean burning = true;

    public FurnaceBurnEvent(Furnace furnace, Item fuel, short burnTime) {
        super(furnace.getBlock());
        this.fuel = fuel;
        this.burnTime = burnTime;
        this.furnace = furnace;
    }

    public Furnace getFurnace() {
        return furnace;
    }

    public Item getFuel() {
        return fuel;
    }

    public short getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(short burnTime) {
        this.burnTime = burnTime;
    }

    public boolean isBurning() {
        return burning;
    }

    public void setBurning(boolean burning) {
        this.burning = burning;
    }
}