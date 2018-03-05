package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.util.Location;

public class PlayerTeleportEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private boolean cancelled;
    private TeleportCause cause;
    private final Location from;
    private Location to;

    public PlayerTeleportEvent(Player player, Location from, Location to, TeleportCause cause) {
        this.player = player;
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

    public void setCause(TeleportCause cause) {
        this.cause = cause;
    }

    public void setTo(Location to) {
        this.to = to;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public enum TeleportCause {
        COMMAND,       // For Nukkit tp command only
        PLUGIN,        // Every plugin
        NETHER_PORTAL, // Teleport using Nether portal
        ENDER_PEARL,   // Teleport by ender pearl
        UNKNOWN        // Unknown cause
    }
}
