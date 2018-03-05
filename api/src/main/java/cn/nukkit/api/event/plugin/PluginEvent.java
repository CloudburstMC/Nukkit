package cn.nukkit.api.event.plugin;

import cn.nukkit.api.event.Event;
import cn.nukkit.api.plugin.Plugin;

public interface PluginEvent extends Event {
    Plugin getPlugin();
}
