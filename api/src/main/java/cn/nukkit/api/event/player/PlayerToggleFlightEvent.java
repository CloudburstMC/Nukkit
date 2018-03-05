package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;

public class PlayerToggleFlightEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final boolean isFlying;
    private boolean cancelled;

    public PlayerToggleFlightEvent(Player player, boolean isFlying) {
        this.player = player;
        this.isFlying = isFlying;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public boolean isFlying() {
        return isFlying;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
