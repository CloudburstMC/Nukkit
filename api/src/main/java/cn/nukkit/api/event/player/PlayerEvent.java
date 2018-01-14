package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Event;
import com.google.common.base.Preconditions;

/**
 * An event that involves a player.
 */
public abstract class PlayerEvent implements Event {

    protected Player player;

    public PlayerEvent(final Player player) {
        this.player = Preconditions.checkNotNull(player, "player");
    }

    /**
     * Get the player in question.
     * @return player
     */
    public Player getPlayer() {
        return player;
    }
}
