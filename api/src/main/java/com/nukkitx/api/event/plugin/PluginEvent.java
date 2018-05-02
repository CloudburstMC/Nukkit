package com.nukkitx.api.event.plugin;

import com.nukkitx.api.event.Event;
import com.nukkitx.api.plugin.Plugin;

public interface PluginEvent extends Event {
    Plugin getPlugin();
}
