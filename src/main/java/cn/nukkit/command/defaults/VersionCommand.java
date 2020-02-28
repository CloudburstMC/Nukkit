package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.player.IPlayer;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginDescription;
import cn.nukkit.utils.TextFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import java.util.List;

import static cn.nukkit.command.args.OfflinePlayerArgument.getOfflinePlayer;
import static cn.nukkit.command.args.OfflinePlayerArgument.offlinePlayer;
import static com.mojang.brigadier.arguments.StringArgumentType.*;

public class VersionCommand extends BaseCommand {
    public static final SimpleCommandExceptionType UNKNOWN_PLUGIN = new SimpleCommandExceptionType(new LiteralMessage("No such plugin")); // TOOD: nukkit.command.version.noSuchPlugin

    public VersionCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("version", "%nukkit.command.version.description");
        // TODO: aliases

        dispatcher.register(literal("version")
                .requires(requirePermission("nukkit.command.version"))
                .then(argument("plugin", string()).executes(this::plugin))
                .executes(this::run));
    }

    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();

        source.sendMessage(new TranslationContainer("nukkit.server.info.extended", source.getServer().getName(),
                source.getServer().getNukkitVersion(),
                "", // codename - not used anymore
                source.getServer().getApiVersion(),
                source.getServer().getVersion(),
                String.valueOf(ProtocolInfo.CURRENT_PROTOCOL)));
        return 1;
    }

    public int plugin(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();

        if(!this.testPermission(source)) {
            return -1;
        }
        String pluginName = getString(context, "plugin");

        final boolean[] found = {false};
        final Plugin[] exactPlugin = { source.getServer().getPluginManager().getPlugin(pluginName) };

        if (exactPlugin[0] == null) {
            pluginName = pluginName.toLowerCase();
            final String finalPluginName = pluginName;

            source.getServer().getPluginManager().getPlugins().forEach((s, p) -> {
                if (s.toLowerCase().contains(finalPluginName)) {
                    exactPlugin[0] = p;
                    found[0] = true;
                }
            });
        } else {
            found[0] = true;
        }

        if (found[0]) {
            PluginDescription desc = exactPlugin[0].getDescription();
            source.sendMessage(TextFormat.DARK_GREEN + desc.getName() + TextFormat.WHITE + " version " + TextFormat.DARK_GREEN + desc.getVersion());

            if (desc.getDescription() != null) {
                source.sendMessage(desc.getDescription());
            }
            if (desc.getWebsite() != null) {
                source.sendMessage("Website: " + desc.getWebsite());
            }

            List<String> authors = desc.getAuthors();
            final String[] authorsString = {""};
            authors.forEach((s) -> authorsString[0] += s);

            if (authors.size() == 1) {
                source.sendMessage("Author: " + authorsString[0]);
            } else if (authors.size() >= 2) {
                source.sendMessage("Authors: " + authorsString[0]);
            }
        } else {
            throw UNKNOWN_PLUGIN.create();
        }
        return 1;
    }
}
