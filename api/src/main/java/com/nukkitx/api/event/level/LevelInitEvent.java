package com.nukkitx.api.event.level;

import com.nukkitx.api.level.Level;

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
