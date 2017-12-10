package cn.nukkit.api.plugin;

import cn.nukkit.api.command.PluginCommand;
import cn.nukkit.api.permission.Permission;
import com.google.common.collect.ImmutableCollection;

public interface PluginContainer extends PluginDescription{
    Plugin getPlugin();

    ImmutableCollection<PluginCommand> getPluginCommands();

    ImmutableCollection<Permission> getPermissions();
}
