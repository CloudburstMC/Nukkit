package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;

public class PlayerAchievementAwardedEvent extends PlayerEvent implements Cancellable{
    private final String achievement;
    private boolean cancelled = false;

    public PlayerAchievementAwardedEvent(final Player player, final String achievementId) {
        super(player);
        this.achievement = achievementId;
    }

    /**
     * Gets the achievement awarded to player
     * @return achievement
     */
    public String getAchievement() {
        return this.achievement;
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