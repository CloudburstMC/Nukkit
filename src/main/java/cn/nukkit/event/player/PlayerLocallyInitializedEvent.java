package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.api.Since;
import cn.nukkit.event.HandlerList;

/**
 * @author Extollite (Nukkit Project)
 */
@Since("1.3.2.0-PN")
public class PlayerLocallyInitializedEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    @Since("1.3.2.0-PN")
    public static HandlerList getHandlers() {
        return handlers;
    }

    @Since("1.3.2.0-PN")
    public PlayerLocallyInitializedEvent(Player player) {
        this.player = player;
    }
}
