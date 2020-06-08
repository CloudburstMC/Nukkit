package cn.nukkit.event.inventory;

import cn.nukkit.blockentity.BlockEntityCampfire;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.block.BlockEvent;
import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CampfireSmeltEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final BlockEntityCampfire campfire;
    private final Item source;
    private Item result;
    private boolean keepItem;

    public CampfireSmeltEvent(BlockEntityCampfire campfire, Item source, Item result) {
        super(campfire.getBlock());
        this.source = source.clone();
        this.source.setCount(1);
        this.result = result;
        this.campfire = campfire;
    }

    public BlockEntityCampfire getCampfire() {
        return campfire;
    }

    public Item getSource() {
        return source;
    }

    public Item getResult() {
        return result;
    }

    public void setResult(Item result) {
        this.result = result;
    }

    public boolean getKeepItem() {
        return keepItem;
    }

    public void setKeepItem(boolean keepItem) {
        this.keepItem = keepItem;
    }
}
