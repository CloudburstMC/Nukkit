package cn.nukkit.api.command;

import cn.nukkit.api.plugin.Plugin;

public interface PluginCommand {

    Plugin getPlugin();

    void setExecutor(CommandExecutor executor);
}
