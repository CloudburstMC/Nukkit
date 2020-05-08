package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.defaults.VanillaCommand;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.TextFormat;

import java.util.Map;
import java.util.StringJoiner;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class PluginsCommand extends VanillaCommand {

    public PluginsCommand(String name) {
        super(name,
                "%nukkit.command.plugins.description",
                "%nukkit.command.plugins.usage",
                new String[]{"pl"}
        );
        this.setPermission("nukkit.command.plugins");
    }

    @Override
    public boolean execute(CommandSender sender, String aliasUsed, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        StringJoiner joiner = new StringJoiner(", ");
        Map<String, Plugin> plugins = sender.getServer().getPluginManager().getPlugins();

        plugins.values().forEach(plugin ->
                joiner.add(plugin.isEnabled() ? TextFormat.GREEN + "" : TextFormat.RED + "" + plugin.getDescription().getFullName()));

        sender.sendMessage(new TranslationContainer("nukkit.command.plugins.success", String.valueOf(plugins.size()), joiner.toString()));
        return true;
    }
}
