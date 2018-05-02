package com.nukkitx.api.event.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.Player;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.event.Cancellable;

public class SignChangeEvent implements BlockEvent, Cancellable {
    private final Block block;
    private final Player player;
    private String[] lines;
    private boolean cancelled;

    public SignChangeEvent(Block block, Player player, String[] lines) {
        this.block = block;
        this.player = player;
        this.lines = lines;
    }

    public Player getPlayer() {
        return player;
    }

    public String[] getLines() {
        return lines;
    }

    public String getLine(int index) {
        Preconditions.checkArgument(index > 0 && index < 4, "index must be from 0 to 3");
        return this.lines[index];
    }

    public void setLine(int index, String line) {
        Preconditions.checkArgument(index > 0 && index < 4, "index must be from 0 to 3");
        this.lines[index] = line;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public Block getBlock() {
        return block;
    }
}
