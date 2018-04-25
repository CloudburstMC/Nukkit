package cn.nukkit.server.command.vanilla;

import cn.nukkit.api.command.CommandData;
import cn.nukkit.api.command.sender.CommandSender;
import cn.nukkit.api.locale.LocaleManager;
import cn.nukkit.api.plugin.Plugin;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.command.VanillaCommand;

import java.util.Locale;
import java.util.Optional;

public class VersionCommand extends VanillaCommand {
    private static CommandData data = CommandData.builder()
            .alias("ver")
            .description("nukkit.command.version.description")
            .usage("nukkit.command.version.usage")
            .build();

    public VersionCommand() {
        super("version", data);
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        LocaleManager localeManager = sender.getServer().getLocaleManager();
        Locale locale = sender.getLocale();

        if (args.length == 0) {
            sender.sendMessage(localeManager.replaceI18n(locale, "nukkit.command.version.server",
                    NukkitServer.NUKKIT_VERSION, NukkitServer.MINECRAFT_VERSION, NukkitServer.API_VERSION));
            return true;
        }

        String pluginName = args[0];
        Optional<Plugin> plugin = sender.getServer().getPluginManager().getPlugin(pluginName);

        if (plugin.isPresent()) {
            sender.sendMessage(localeManager.replaceI18n(locale, "nukkit.command.version.plugin",
                    plugin.get().getName(), plugin.get().getVersion()));
        } else {
            sender.sendMessage(localeManager.replaceI18n(locale, "nukkit.command.version.noSuchPlugin"));
        }
        return true;
    }
}
