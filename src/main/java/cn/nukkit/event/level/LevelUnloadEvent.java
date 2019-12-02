package cn.nukkit.event.level;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Level;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LevelUnloadEvent extends LevelEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public LevelUnloadEvent(Level level) {
        super(level);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}
