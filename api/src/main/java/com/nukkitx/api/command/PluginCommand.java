package com.nukkitx.api.command;

import com.nukkitx.api.plugin.PluginContainer;

public interface PluginCommand extends Command {

    PluginContainer getPlugin();
}
