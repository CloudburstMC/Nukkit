package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import static com.mojang.brigadier.arguments.StringArgumentType.*;

public class SayCommand extends BaseCommand {

    public SayCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("say", "%nukkit.command.say.description");
        setPermission("nukkit.command.say");

        dispatcher.register(literal("say")
                .then(argument("message", greedyString()).executes(context ->
                        run(context, getString(context, "message")))));
    }

    public int run(CommandContext<CommandSource> context, String message) throws CommandSyntaxException {
        CommandSource source = context.getSource();

        if (!this.testPermission(source)) {
            return -1;
        }

        String senderString;
        if (source instanceof Player) {
            senderString = ((Player) source).getDisplayName();
        } else if (source instanceof ConsoleCommandSender) {
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
