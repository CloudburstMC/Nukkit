package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.event.TranslationContainer;
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
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        final String[] list = {""};
        Map<String, Plugin> plugins = sender.getServer().getPluginManager().getPlugins();
        plugins.forEach((s, p) -> {
            if (list[0].length() > 0) {
                list[0] += TextFormat.WHITE + ", ";
            }
            list[0] += p.isEnabled() ? TextFormat.GREEN : TextFormat.RED;
            list[0] += p.getDescription().getFullName();
        });
        String pluginsCountString = String.valueOf(plugins.size());
        sender.sendMessage(new TranslationContainer("nukkit.command.plugins.success", new String[]{pluginsCountString, list[0]}));
        return true;
    }
}
