package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Event;

public abstract class PlayerEvent implements Event {
    protected Player player;

    public PlayerEvent(final Player player) {
        this.player = player;
    }

    /**
     * Gets the player involved in the event.
     * @return player
     */
    public Player getPlayer() {
        return player;
    }
}
