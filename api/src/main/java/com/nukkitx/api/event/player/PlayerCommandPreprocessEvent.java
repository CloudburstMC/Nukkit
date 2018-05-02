package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.event.Cancellable;

public class PlayerCommandPreprocessEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final String command;
    private boolean cancelled;

    public PlayerCommandPreprocessEvent(final Player player, String command) {
        this.player = player;
        this.command = command;
    }

    public String getCommand() {
        return command;
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
