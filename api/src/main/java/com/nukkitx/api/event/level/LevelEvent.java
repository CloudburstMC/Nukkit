package com.nukkitx.api.event.level;

import com.nukkitx.api.event.Event;
import com.nukkitx.api.level.Level;

public interface LevelEvent extends Event {
    Level getLevel();
}
