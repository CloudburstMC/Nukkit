package cn.nukkit.event.level;

import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Level;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@Deprecated()
public class LevelInitEvent extends LevelEvent {

    private static final HandlerList handlers = new HandlerList();

    public LevelInitEvent(Level level) {
        super(level);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}
