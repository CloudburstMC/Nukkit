package cn.nukkit.command.defaults.vanilla;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.defaults.VanillaCommand;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;

import java.util.Arrays;

import static cn.nukkit.command.args.builder.OptionalArgumentBuilder.optionalArg;
import static cn.nukkit.command.args.builder.RequiredArgumentBuilder.requiredArg;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class KickCommand extends VanillaCommand {

    public KickCommand(String name) {
        super(name, "commands.kick.description", "/kick <player> [reason]");
        this.setPermission("nukkit.command.kick");

        registerOverload()
                .then(requiredArg("player", CommandParamType.TARGET))
                .then(optionalArg("reason", CommandParamType.RAWTEXT));
    }

    @Override
    public boolean execute(CommandSender sender, String aliasUsed, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", usageMessage));
            return false;
        }

        String name = args[0];
        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        Player player = sender.getServer().getPlayer(name);
        if (player == null) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
            return true;
        }
        player.kick(PlayerKickEvent.Reason.KICKED_BY_ADMIN, reason);

        if (reason.length() >= 1) {
            CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("commands.kick.success.reason", player.getName(), reason));
        } else {
            CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("commands.kick.success", player.getName()));
        }
        return true;
    }
}
