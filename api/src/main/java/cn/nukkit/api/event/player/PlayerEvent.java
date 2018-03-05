package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Event;

/**
 * An event that involves a player.
 */
public interface PlayerEvent extends Event {

    /**
     * Get the player in question.
     * @return player
     */
    Player getPlayer();
}
