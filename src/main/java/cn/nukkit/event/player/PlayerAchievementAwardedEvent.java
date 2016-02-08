package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class PlayerAchievementAwardedEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected String achievement;

    public PlayerAchievementAwardedEvent(Player player, String achievementId) {
        this.player = player;
        this.achievement = achievementId;
    }

    public String getAchievement() {
        return this.achievement;
    }
}
