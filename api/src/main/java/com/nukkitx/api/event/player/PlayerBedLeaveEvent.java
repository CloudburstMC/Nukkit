package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.block.Block;

public class PlayerBedLeaveEvent implements PlayerEvent {
    private final Player player;
    private final Block bed;

    public PlayerBedLeaveEvent(Player player, Block bed) {
        this.player = player;
        this.bed = bed;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public Block getBed() {
        return bed;
    }
}
