package cn.nukkit.api.event.level;

import cn.nukkit.api.level.Level;

public class LevelUnloadEvent implements LevelEvent {
    private final Level level;

    public LevelUnloadEvent(Level level) {
        this.level = level;
    }

    @Override
    public Level getLevel() {
        return level;
    }
}
