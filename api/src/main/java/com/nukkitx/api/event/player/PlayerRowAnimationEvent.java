package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;

public class PlayerRowAnimationEvent extends PlayerAnimationEvent {
    private final float rowingTime;

    public PlayerRowAnimationEvent(Player player, Player.Animation animation, float rowingTime) {
        super(player, animation);
        this.rowingTime = rowingTime;
    }

    public float getRowingTime() {
        return rowingTime;
    }
}
