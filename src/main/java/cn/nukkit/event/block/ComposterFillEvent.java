package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

public class ComposterFillEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Item item;
    private final int chance;
    private boolean success;

    public ComposterFillEvent(Block block, Player player, Item item, int chance, boolean success) {
        super(block);
        this.player = player;
        this.item = item;
        this.chance = chance;
        this.success = success;
    }

    public Player getPlayer() {
        return player;
    }

    public Item getItem() {
        return item;
    }

    public int getChance() {
        return chance;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
