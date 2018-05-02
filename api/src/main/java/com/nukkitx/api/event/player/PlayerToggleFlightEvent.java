package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.event.Cancellable;

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
