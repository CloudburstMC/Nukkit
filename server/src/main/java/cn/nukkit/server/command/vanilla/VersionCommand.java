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
    private static final String SERVER_VERSION_I18N = "nukkit.command.version.server";
    private static final String PLUGIN_VERSION_I18N = "nukkit.command.version.plugin";
    private static final String PLUGIN_NOT_FOUND_I18N = "nukkit.command.version.noSuchPlugin";
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
            sender.sendMessage(localeManager.replaceI18n(locale, SERVER_VERSION_I18N, NukkitServer.NUKKIT_VERSION,
                    NukkitServer.MINECRAFT_VERSION, NukkitServer.API_VERSION));
            return true;
        }

        String pluginName = args[0];
        Optional<Plugin> plugin = sender.getServer().getPluginManager().getPlugin(pluginName);

        if (plugin.isPresent()) {
            sender.sendMessage(localeManager.replaceI18n(locale, PLUGIN_VERSION_I18N,
                    plugin.get().getName(), plugin.get().getVersion()));
        } else {
            sender.sendMessage(localeManager.replaceI18n(locale, PLUGIN_NOT_FOUND_I18N));
        }
        return true;
    }
}
