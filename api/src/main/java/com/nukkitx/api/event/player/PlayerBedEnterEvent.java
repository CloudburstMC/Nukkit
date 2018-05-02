package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.event.Cancellable;

public class PlayerBedEnterEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final Block bed;
    private boolean cancelled;

    public PlayerBedEnterEvent(Player player, Block bed) {
        this.player = player;
        this.bed = bed;
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

    public Block getBed() {
        return bed;
    }
}
