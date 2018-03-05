package cn.nukkit.api.event.level;

import cn.nukkit.api.level.Level;

public class LevelLoadEvent implements LevelEvent {
    private final Level level;

    public LevelLoadEvent(Level level) {
        this.level = level;
    }

    @Override
    public Level getLevel() {
        return level;
    }
}
