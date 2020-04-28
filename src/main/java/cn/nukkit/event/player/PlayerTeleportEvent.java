package cn.nukkit.event.player;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Location;
import cn.nukkit.player.Player;

public class PlayerTeleportEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final TeleportCause cause;
    private final Location from;
    private final Location to;

    public PlayerTeleportEvent(Player player, Location from, Location to, TeleportCause cause) {
        super(player);
        this.from = from;
        this.to = to;
        this.cause = cause;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public TeleportCause getCause() {
        return cause;
    }


    public enum TeleportCause {
        COMMAND,       // For Nukkit tp command only
        PLUGIN,        // Every plugin
        NETHER_PORTAL, // Teleport using Nether portal
        ENDER_PEARL,   // Teleport by ender pearl
        CHORUS_FRUIT,  // Teleport by chorus fruit
        UNKNOWN        // Unknown cause
    }
}
