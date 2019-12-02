package cn.nukkit.event.level;

import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Level;

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
