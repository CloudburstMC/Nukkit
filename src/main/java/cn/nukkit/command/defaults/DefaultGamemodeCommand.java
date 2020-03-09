package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import static cn.nukkit.command.args.GameModeArgument.gamemode;
import static cn.nukkit.command.args.GameModeArgument.getGamemode;
import static cn.nukkit.command.args.PlayerArgument.player;

public class DefaultGamemodeCommand extends BaseCommand {

    public DefaultGamemodeCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("defaultgamemode", "%nukkit.command.defaultgamemode.description");

        dispatcher.register(literal("defaultgamemode")
                .requires(requirePermission("nukkit.command.defaultgamemode"))
                .then(argument("mode", gamemode()).executes(this::run)));
    }

    public int run(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        String mode = getGamemode(context, "mode");

        int gameMode = Server.getGamemodeFromString(mode);

        source.getServer().setPropertyInt("gamemode", gameMode);
        source.sendMessage(new TranslationContainer("commands.defaultgamemode.success", new String[]{Server.getGamemodeString(gameMode)}));
        return 1;
    }
}
