package com.nukkitx.api.command;

import com.nukkitx.api.plugin.Plugin;

public interface PluginCommand extends Command {

    Plugin getPlugin();
}
