package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.event.Cancellable;

public class PlayerAchievementAwardedEvent implements PlayerEvent, Cancellable {
    private final Player player;
    private final String achievement;
    private boolean cancelled = false;

    public PlayerAchievementAwardedEvent(final Player player, final String achievementId) {
        this.player = player;
        this.achievement = achievementId;
    }

    public String getAchievement() {
        return achievement;
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
    public Player getPlayer() {
        return player;
    }
}