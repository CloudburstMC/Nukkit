package cn.nukkit.event.player;

import cn.nukkit.event.Event;
import cn.nukkit.player.Player;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class PlayerEvent extends Event {

    private final Player player;

    public PlayerEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
