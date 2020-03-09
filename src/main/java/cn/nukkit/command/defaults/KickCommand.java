package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.permission.BanList;
import cn.nukkit.player.IPlayer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import static cn.nukkit.command.args.OfflinePlayerArgument.getOfflinePlayer;
import static cn.nukkit.command.args.OfflinePlayerArgument.offlinePlayer;
import static cn.nukkit.command.args.PlayerArgument.getPlayer;
import static cn.nukkit.command.args.PlayerArgument.player;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;

public class KickCommand extends BaseCommand {

    public KickCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("kick", "%nukkit.command.kick.description");

        // TODO: This doesnt work. Stacktrace: https://hastebin.com/teyuyopome.apache
        dispatcher.register(literal("kick")
                .requires(requirePermission("nukkit.command.kick"))
                .then(argument("player", player()).executes(this::run))
                .then(argument("reason", greedyString()).executes(this::run)));
    }

    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        Player target = getPlayer(context, "player");
        String reason = getString(context, "reason");

        target.kick(PlayerKickEvent.Reason.KICKED_BY_ADMIN, reason);

        if (reason != null) {
            sendAdminMessage(source, new TranslationContainer("commands.kick.success.reason", target.getName(), reason));
        } else {
            sendAdminMessage(source, new TranslationContainer("commands.kick.success", target.getName()));
        }

        return 1;
    }
}
