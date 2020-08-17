package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.api.Since;
import cn.nukkit.event.HandlerList;

/**
 * @author Extollite (Nukkit Project)
 */
@Since("1.4.0.0-PN")
public class PlayerLocallyInitializedEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    @Since("1.4.0.0-PN")
    public PlayerLocallyInitializedEvent(Player player) {
        this.player = player;
    }
}
