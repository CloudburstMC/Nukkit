package cn.nukkit.api.event.level;

import cn.nukkit.api.event.Event;
import cn.nukkit.api.level.Level;

public interface LevelEvent extends Event {
    Level getLevel();
}
