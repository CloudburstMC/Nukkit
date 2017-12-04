package cn.nukkit.server.event.player;

import cn.nukkit.server.Player;
import cn.nukkit.server.event.Cancellable;
import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.level.Level;
import cn.nukkit.server.level.Location;
import cn.nukkit.server.level.Position;
import cn.nukkit.server.math.Vector3;

public class PlayerTeleportEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private TeleportCause cause;
    private Location from;
    private Location to;

    private PlayerTeleportEvent(Player player) {
        this.player = player;
    }

    public PlayerTeleportEvent(Player player, Location from, Location to, TeleportCause cause) {
        this(player);
        this.from = from;
        this.to = to;
        this.cause = cause;
    }

    public PlayerTeleportEvent(Player player, Vector3 from, Vector3 to, TeleportCause cause) {
        this(player);
        this.from = vectorToLocation(player.getLevel(), from);
        this.from = vectorToLocation(player.getLevel(), to);
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

    private Location vectorToLocation(Level baseLevel, Vector3 vector) {
        if (vector instanceof Location) return (Location) vector;
        if (vector instanceof Position) return ((Position) vector).getLocation();
        return new Location(vector.getX(), vector.getY(), vector.getZ(), 0, 0, baseLevel);
    }


    public enum TeleportCause {
        COMMAND,       // For Nukkit tp command only
        PLUGIN,        // Every plugin
        NETHER_PORTAL, // Teleport using Nether portal
        ENDER_PEARL,   // Teleport by ender pearl
        UNKNOWN        // Unknown cause
    }
}
