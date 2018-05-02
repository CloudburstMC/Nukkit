package com.nukkitx.api.event.block;

import com.nukkitx.api.Player;
import com.nukkitx.api.block.Block;

public class DoorToggleEvent extends BlockUpdateEvent {
    private Player player;

    public DoorToggleEvent(Block block, Player player) {
        super(block);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
