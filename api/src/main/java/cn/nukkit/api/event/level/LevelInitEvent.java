package cn.nukkit.api.event.level;

import cn.nukkit.api.level.Level;

public class LevelInitEvent implements LevelEvent {
    private final Level level;

    public LevelInitEvent(Level level) {
        this.level = level;
    }

    @Override
    public Level getLevel() {
        return level;
    }
}
