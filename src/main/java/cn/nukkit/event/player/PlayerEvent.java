package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Event;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class PlayerEvent extends Event {
    protected Player player;

    public Player getPlayer() {
        return player;
    }
}
