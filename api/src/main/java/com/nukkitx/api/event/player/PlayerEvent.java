package com.nukkitx.api.event.player;

import com.nukkitx.api.Player;
import com.nukkitx.api.event.Event;

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
