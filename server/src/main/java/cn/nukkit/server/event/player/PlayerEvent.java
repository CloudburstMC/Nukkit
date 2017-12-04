package cn.nukkit.server.event.player;

import cn.nukkit.server.Player;
import cn.nukkit.server.event.Event;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class PlayerEvent extends Event {
    protected Player player;

    public Player getPlayer() {
        return player;
    }
}
