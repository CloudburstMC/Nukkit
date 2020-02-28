package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.TextFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Map;

public class PluginsCommand extends BaseCommand {

    public PluginsCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("plugins", "%nukkit.command.plugins.description");
        // TODO: aliases

        dispatcher.register(literal("plugins")
                .requires(requirePermission("nukkit.command.plugins"))
                .executes(this::run));
    }

    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        StringBuilder list = new StringBuilder();
        Map<String, Plugin> plugins = source.getServer().getPluginManager().getPlugins();

        for (Plugin plugin : plugins.values()) {
            if (list.length() > 0) {
                list.append(TextFormat.WHITE + ", ");
            }

            list.append(plugin.isEnabled() ? TextFormat.GREEN : TextFormat.RED);
            list.append(plugin.getDescription().getFullName());
        }

        source.sendMessage(new TranslationContainer("nukkit.command.plugins.success", String.valueOf(plugins.size()), list.toString()));
        return 1;
    }
}
