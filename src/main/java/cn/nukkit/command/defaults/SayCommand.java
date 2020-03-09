package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.ConsoleCommandSource;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import static com.mojang.brigadier.arguments.StringArgumentType.*;

public class SayCommand extends BaseCommand {

    public SayCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("say", "commands.say.description");

        dispatcher.register(literal("say")
                .requires(requirePermission("nukkit.command.say"))
                .then(argument("message", greedyString()).executes(this::run)));
    }

    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        String message = getString(context, "message");

        String senderString;
        if (source instanceof Player) {
            senderString = ((Player) source).getDisplayName();
        } else if (source instanceof ConsoleCommandSource) {
            senderString = "Server";
        } else {
            senderString = source.getName();
        }

        source.getServer().broadcastMessage(new TranslationContainer(
                TextFormat.LIGHT_PURPLE + "%chat.type.announcement",
                senderString, TextFormat.LIGHT_PURPLE + message));

        return 1;
    }
}
