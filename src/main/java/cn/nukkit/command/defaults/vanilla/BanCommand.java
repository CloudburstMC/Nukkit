package cn.nukkit.command.defaults.vanilla;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.defaults.VanillaCommand;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;

import static cn.nukkit.command.args.builder.OptionalArgumentBuilder.optionalArg;
import static cn.nukkit.command.args.builder.RequiredArgumentBuilder.requiredArg;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BanCommand extends VanillaCommand {

    public BanCommand(String name) {
        super(name, "commands.ban.description", "/ban <player> [reason]");
        this.setPermission("nukkit.command.ban.player");

        registerOverload()
                .then(requiredArg("player", CommandParamType.TARGET))
                .then(optionalArg("reason", CommandParamType.STRING));
    }

    @Override
    public boolean execute(CommandSender sender, String aliasUsed, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));

            return false;
        }

        String name = args[0];
        String reason = "";
        for (int i = 1; i < args.length; i++) {
            reason += args[i] + " ";
        }

        if (reason.length() > 0) {
            reason = reason.substring(0, reason.length() - 1);
        }

        sender.getServer().getNameBans().addBan(name, reason, null, sender.getName());

        Player player = sender.getServer().getPlayerExact(name);
        if (player != null) {
            player.kick(PlayerKickEvent.Reason.NAME_BANNED, !reason.isEmpty() ? "Banned by admin. Reason: " + reason : "Banned by admin");
        }

        CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.ban.success", player != null ? player.getName() : name));

        return true;
    }
}
