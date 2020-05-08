package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.defaults.VanillaCommand;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.network.ProtocolInfo;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginDescription;
import cn.nukkit.utils.TextFormat;

import java.util.List;

import static cn.nukkit.command.args.builder.OptionalArgumentBuilder.optionalArg;

/**
 * Created on 2015/11/12 by xtypr.
 *
 * @author lukeeey
 * @author xtypr
 */
public class VersionCommand extends VanillaCommand {

    public VersionCommand(String name) {
        super(name,
                "%nukkit.command.version.description",
                "/version",
                new String[]{"ver", "about"}
        );
        this.setPermission("nukkit.command.version;nukkit.command.version.plugin");

        registerOverload().then(optionalArg("pluginName", CommandParamType.STRING));
    }

    @Override
    public boolean execute(CommandSender sender, String aliasUsed, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("nukkit.server.info.extended", sender.getServer().getName(),
                    sender.getServer().getNukkitVersion(),
                    sender.getServer().getApiVersion(),
                    sender.getServer().getVersion(),
                    String.valueOf(ProtocolInfo.getDefaultProtocolVersion())));
        } else {
            if(!sender.hasPermission("nukkit.command.version.plugin")) {
                return true;
            }
            Plugin plugin = sender.getServer().getPluginManager().getPlugin(args[0].trim());

            if(plugin == null) {
                sender.sendMessage(new TranslationContainer("nukkit.command.version.noSuchPlugin"));
                return true;
            }

            PluginDescription desc = plugin.getDescription();

            sender.sendMessage(TextFormat.DARK_GREEN + desc.getName() + TextFormat.WHITE + " version " + TextFormat.DARK_GREEN + desc.getVersion());
            if (desc.getDescription() != null) {
                sender.sendMessage(desc.getDescription());
            }
            if (desc.getWebsite() != null) {
                sender.sendMessage("Website: " + desc.getWebsite());
            }
            if(desc.getAuthors().size() == 1) {
                sender.sendMessage("Author: " + desc.getAuthors().get(0));
            } else if(desc.getAuthors().size() >= 2) {
                sender.sendMessage("Authors: " + String.join(", ", desc.getAuthors()));
            }
        }
        return true;
    }
}
