package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.HandlerList;

public class PlayerBedLeaveEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Block bed;

    public PlayerBedLeaveEvent(Player player, Block bed) {
        this.player = player;
        this.bed = bed;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Block getBed() {
        return bed;
    }
}
