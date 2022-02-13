package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class SignGlowEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Player player;
    private final boolean glowing;

    public SignGlowEvent(Block block, Player player, boolean glowing) {
        super(block);
        this.player = player;
        this.glowing = glowing;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isGlowing() {
        return this.glowing;
    }
}
