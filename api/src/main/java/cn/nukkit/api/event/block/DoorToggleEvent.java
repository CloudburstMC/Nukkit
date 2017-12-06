package cn.nukkit.api.event.block;

import cn.nukkit.api.Player;
import cn.nukkit.server.block.Block;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;

/**
 * Created by Snake1999 on 2016/1/22.
 * Package cn.nukkit.api.event.block in project nukkit.
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
