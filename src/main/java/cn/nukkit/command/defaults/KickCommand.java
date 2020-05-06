package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;

import java.util.StringJoiner;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class KickCommand extends Command {

    public KickCommand() {
        super("kick", CommandData.builder("kick")
                .setDescription("commands.kick.description")
                .setUsageMessage("/kick <player> [reason]")
                .setPermissions("nukkit.command.kick")
                .setParameters(new CommandParameter[]{
                        new CommandParameter("player", CommandParamType.TARGET, false),
                        new CommandParameter("reason", true)
                })
                .build());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length == 0) {
            return false;
        }

        String name = args[0];

        StringJoiner reason = new StringJoiner(" ");
        for (int i = 1; i < args.length; i++) {
            reason.add(args[i]);
        }

        Player player = sender.getServer().getPlayer(name);
        if (player != null) {
            player.kick(PlayerKickEvent.Reason.KICKED_BY_ADMIN, reason.toString());
            if (reason.length() >= 1) {
                CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.kick.success.reason", player.getName(), reason.toString())
                );
            } else {
                CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.kick.success", player.getName()));
            }
        } else {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
        }

        return true;
    }
}
