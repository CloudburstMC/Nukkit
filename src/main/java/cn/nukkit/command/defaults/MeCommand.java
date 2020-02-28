package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;

public class MeCommand extends BaseCommand {

    public MeCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("me", "%nukkit.command.me.description");
        setPermission("nukkit.command.me");

        dispatcher.register(literal("me")
                .then(argument("action...", greedyString()).executes(this::run)));
    }

    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        String message = getString(context, "action...");

        if (!this.testPermission(source)) {
            return -1;
        }

        String name = (source instanceof Player) ? ((Player) source).getDisplayName() : source.getName();
        source.getServer().broadcastMessage(new TranslationContainer("chat.type.emote", name, TextFormat.WHITE + message));
        return 1;
    }
}
