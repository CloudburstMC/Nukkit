package cn.nukkit.api.event.level;

import cn.nukkit.server.event.HandlerList;
import cn.nukkit.server.level.Level;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LevelLoadEvent extends LevelEvent {

    private static final HandlerList handlers = new HandlerList();

    public LevelLoadEvent(Level level) {
        super(level);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}
