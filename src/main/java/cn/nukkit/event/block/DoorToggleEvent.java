package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.player.Player;

/**
 * Created by Snake1999 on 2016/1/22.
 * Package cn.nukkit.event.block in project nukkit.
 */
public class DoorToggleEvent extends BlockUpdateEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private Player player;

    public DoorToggleEvent(Block block, Player player) {
        super(block);
        this.player = player;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
