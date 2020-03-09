package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import java.util.Objects;

import static cn.nukkit.command.args.PlayerArgument.getPlayer;
import static cn.nukkit.command.args.PlayerArgument.player;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;

public class TellCommand extends BaseCommand {

    public TellCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("tell", "commands.tell.description"); // TODO: aliases (w, msg)

        dispatcher.register(literal("tell")
                .requires(requirePermission("nukkit.command.tell"))
                .then(argument("player", player())
                        .then(argument("message", greedyString())
                            .executes(this::run))));
    }

    public int run(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        Player player = getPlayer(context, "player");
        String message = getString(context, "message");

        if (Objects.equals(player, source)) {
            source.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.message.sameTarget"));
            return 1;
        }

        String displayName = (source instanceof Player ? ((Player) source).getDisplayName() : source.getName());

        source.sendMessage("[" + source.getName() + " -> " + player.getDisplayName() + "] " + message);
        player.sendMessage("[" + displayName + " -> me] " + message);
        return 1;
    }
}
