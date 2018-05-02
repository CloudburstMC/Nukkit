package com.nukkitx.api.event.level;

import com.nukkitx.api.level.Level;

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
