package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.TextFormat;

import java.util.Map;

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
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        this.sendPluginList(sender);
        return true;
    }

    private void sendPluginList(CommandSender sender) {
        StringBuilder list = new StringBuilder();
        Map<String, Plugin> plugins = sender.getServer().getPluginManager().getPlugins();
        for (Plugin plugin : plugins.values()) {
            if (list.length() > 0) {
                list.append(TextFormat.WHITE + ", ");
            }
            list.append(plugin.isEnabled() ? TextFormat.GREEN : TextFormat.RED);
            list.append(plugin.getDescription().getFullName());
        }

        sender.sendMessage(new TranslationContainer("nukkit.command.plugins.success", String.valueOf(plugins.size()), list.toString()));
    }
}
