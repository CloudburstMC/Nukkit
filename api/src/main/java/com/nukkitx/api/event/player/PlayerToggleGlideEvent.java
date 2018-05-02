package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.event.Cancellable;

public class PlayerToggleGlideEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final boolean isGliding;
    private boolean cancelled;

    public PlayerToggleGlideEvent(Player player, boolean isSneaking) {
        this.player = player;
        this.isGliding = isSneaking;
    }

    public boolean isGliding() {
        return isGliding;
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
}
