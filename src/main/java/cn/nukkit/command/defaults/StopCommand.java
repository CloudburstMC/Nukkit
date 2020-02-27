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
        setPermission("nukkit.command.stop");

        dispatcher.register(literal("stop").executes(this::run));
    }

    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();

        if (!this.testPermission(source)) {
            return -1;
        }

        sendAdminMessage(source, new TranslationContainer("commands.stop.start"));
        source.getServer().shutdown();
        return 1;
    }
}
