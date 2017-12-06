package cn.nukkit.server.command.defaults;

import cn.nukkit.api.event.player.PlayerKickEvent;
import cn.nukkit.api.message.TranslatedMessage;
import cn.nukkit.server.Player;
import cn.nukkit.server.command.Command;
import cn.nukkit.server.command.data.CommandParameter;
import cn.nukkit.server.utils.TextFormat;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.server.command.defaults in project Nukkit .
 */
public class KickCommand extends VanillaCommand {

    public KickCommand(String name) {
        super(name, "%nukkit.command.kick.description", "%commands.kick.usage");
        this.setPermission("nukkit.command.kick");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParameter.ARG_TYPE_TARGET, false),
                new CommandParameter("reason", true)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(new TranslatedMessage("commands.generic.usage", this.usageMessage));
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

        Player player = sender.getServer().getPlayer(name);
        if (player != null) {
            player.kick(PlayerKickEvent.Reason.KICKED_BY_ADMIN, reason);
            if (reason.length() >= 1) {
                Command.broadcastCommandMessage(sender, new TranslatedMessage("commands.kick.success.reason", new String[]{player.getName(), reason})
                );
            } else {
                Command.broadcastCommandMessage(sender, new TranslatedMessage("commands.kick.success", player.getName()));
            }
        } else {
            sender.sendMessage(new TranslatedMessage(TextFormat.RED + "%commands.generic.player.notFound"));
        }

        return true;
    }
}
