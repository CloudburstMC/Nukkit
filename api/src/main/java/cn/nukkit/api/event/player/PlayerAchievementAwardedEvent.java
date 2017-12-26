package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerAchievementAwardedEvent extends PlayerEvent implements Cancellable {

    private final String achievement;
    private boolean cancelled = false;

    public PlayerAchievementAwardedEvent(final Player player, final String achievementId) {
        super(player);
        this.achievement = achievementId;
    }
}