package cn.nukkit.server.event.player;

import cn.nukkit.server.Player;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.level.Location;

public class PlayerMoveEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Location from;
    private Location to;

    private boolean resetBlocksAround;

    public PlayerMoveEvent(Player player, Location from, Location to) {
        this(player, from, to, true);
    }

    public PlayerMoveEvent(Player player, Location from, Location to, boolean resetBlocks) {
        this.player = player;
        this.from = from;
        this.to = to;
        this.resetBlocksAround = resetBlocks;
    }

    public Location getFrom() {
        return from;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public Location getTo() {
        return to;
    }

    public void setTo(Location to) {
        this.to = to;
    }

    public boolean isResetBlocksAround() {
        return resetBlocksAround;
    }

    public void setResetBlocksAround(boolean value) {
        this.resetBlocksAround = value;
    }

    @Override
    public void setCancelled() {
        super.setCancelled();
    }
}
