package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.lang.TranslationContainer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class StopCommand extends BaseCommand {

    public StopCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("stop", "%nukkit.command.stop.description");

        dispatcher.register(literal("stop")
                .requires(requirePermission("nukkit.command.stop"))
                .executes(this::run));
    }

    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();

        sendAdminMessage(source, new TranslationContainer("commands.stop.start"));
        source.getServer().shutdown();
        return 1;
    }
}
