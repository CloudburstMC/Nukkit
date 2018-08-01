package com.nukkitx.api.event.plugin;

import com.nukkitx.api.event.Event;

public interface PluginEvent extends Event {
    <T> T getPlugin();
}
