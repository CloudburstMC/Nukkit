package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.args.PlayerArgument;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.permission.BanList;
import cn.nukkit.player.IPlayer;
import cn.nukkit.player.OfflinePlayer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import lombok.extern.log4j.Log4j2;

import java.util.Collection;

import static cn.nukkit.command.args.OfflinePlayerArgument.getOfflinePlayer;
import static cn.nukkit.command.args.OfflinePlayerArgument.offlinePlayer;
import static cn.nukkit.command.args.PlayerArgument.*;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;

public class BanCommand extends BaseCommand {
    public static final SimpleCommandExceptionType ALREADY_BANNED = new SimpleCommandExceptionType(new LiteralMessage("That player is already banned"));

    public BanCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("ban", "%nukkit.command.ban.player.description");
        setPermission("nukkit.command.ban.player");

        // TODO: This doesnt work. Stacktrace: https://hastebin.com/teyuyopome.apache
        dispatcher.register(literal("kick")
                .then(argument("player", player()).executes(this::run))
                .then(argument("reason", greedyString()).executes(this::run)));
    }

    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        BanList banList = source.getServer().getNameBans();

        Player target = getPlayer(context, "player");
        String reason = getString(context, "reason");

        if(!testPermission(source)) {
            return -1;
        }
        if(target.isBanned()) {
            throw ALREADY_BANNED.create();
        }

        // TODO: BanEntry.of()?
        banList.addBan(target.getName(), reason, null, source.getName());

        if(target instanceof Player) {
            ((Player) target).kick(PlayerKickEvent.Reason.NAME_BANNED, reason != null ? "Banned by admin. Reason: " + reason : "Banned by admin");
        }

        sendAdminMessage(source, "Ban successful: " + target.getName());
        return 1;
    }
}
