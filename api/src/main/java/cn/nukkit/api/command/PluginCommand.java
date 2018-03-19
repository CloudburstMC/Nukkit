package cn.nukkit.api.command;

import cn.nukkit.api.plugin.Plugin;

public interface PluginCommand extends Command {

    Plugin getPlugin();
}
