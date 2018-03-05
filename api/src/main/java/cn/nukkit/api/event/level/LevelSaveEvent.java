package cn.nukkit.api.event.level;

import cn.nukkit.api.level.Level;

public class LevelSaveEvent implements LevelEvent {
    private final Level level;

    public LevelSaveEvent(Level level) {
        this.level = level;
    }

    @Override
    public Level getLevel() {
        return level;
    }
}
