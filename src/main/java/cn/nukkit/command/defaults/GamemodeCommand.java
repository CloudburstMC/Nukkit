package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import static cn.nukkit.command.args.GameModeArgument.gamemode;
import static cn.nukkit.command.args.GameModeArgument.getGamemode;
import static cn.nukkit.command.args.PlayerArgument.getPlayer;
import static cn.nukkit.command.args.PlayerArgument.player;

public class GamemodeCommand extends BaseCommand {

    public GamemodeCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("gamemode", "%nukkit.command.gamemode.description"); // TODO: aliases (gm)

        setPermission("nukkit.command.gamemode.survival;" +
                "nukkit.command.gamemode.creative;" +
                "nukkit.command.gamemode.adventure;" +
                "nukkit.command.gamemode.spectator;" +
                "nukkit.command.gamemode.other");

        dispatcher.register(literal("gamemode")
                .then(argument("mode", gamemode())
                        .requires(requireSourceBePlayer())
                        .executes(this::run)
                .then(argument("player", player()).executes(this::other))));
    }

    public int run(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        String mode = getGamemode(context, "mode");

        int gameMode = Server.getGamemodeFromString(mode);

        if ((gameMode == 0 && !source.hasPermission("nukkit.command.gamemode.survival")) ||
                (gameMode == 1 && !source.hasPermission("nukkit.command.gamemode.creative")) ||
                (gameMode == 2 && !source.hasPermission("nukkit.command.gamemode.adventure")) ||
                (gameMode == 3 && !source.hasPermission("nukkit.command.gamemode.spectator"))) {
            source.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
            return 1;
        }

        if (!((Player) source).setGamemode(gameMode)) {
            source.sendMessage("Game mode update for " + source.getName() + " failed");
        } else {
            sendAdminMessage(source, new TranslationContainer("commands.gamemode.success.self", Server.getGamemodeString(gameMode)));
        }
        return 1;
    }

    public int other(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        String mode = getGamemode(context, "mode");
        Player target = getPlayer(context, "player");

        int gameMode = Server.getGamemodeFromString(mode);

        if (source.hasPermission("nukkit.command.gamemode.other")) {
            if (target == null) {
                source.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                return 1;
            }
        } else {
            source.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
            return 1;
        }

        target.sendMessage(new TranslationContainer("gameMode.changed"));
        sendAdminMessage(source, new TranslationContainer("commands.gamemode.success.other", target.getName(), Server.getGamemodeString(gameMode)));
        return 1;
    }
}
