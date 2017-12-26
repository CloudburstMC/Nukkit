package cn.nukkit.api.event.player;

import cn.nukkit.api.Location;
import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.level.Level;
import com.flowpowered.math.vector.Vector3f;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerTeleportEvent extends PlayerEvent implements Cancellable {

    private boolean cancelled;

    private TeleportCause cause;
    private Location from;
    private Location to;

    public PlayerTeleportEvent(Player player, Location from, Location to, TeleportCause cause) {
        super(player);
        this.from = from;
        this.to = to;
        this.cause = cause;
    }

    public PlayerTeleportEvent(Player player, Vector3f from, Vector3f to, TeleportCause cause) {
        super(player);
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

    private Location vectorToLocation(Level baseLevel, Vector3f vector) {
        return new Location(baseLevel, vector);
    }


    public enum TeleportCause {
        COMMAND,       // For Nukkit tp command only
        PLUGIN,        // Every plugin
        NETHER_PORTAL, // Teleport using Nether portal
        ENDER_PEARL,   // Teleport by ender pearl
        UNKNOWN        // Unknown cause
    }
}
