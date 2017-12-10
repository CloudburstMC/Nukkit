package cn.nukkit.server.command.defaults;

import cn.nukkit.api.command.CommandExecutorSource;
import cn.nukkit.api.event.player.PlayerKickEvent;
import cn.nukkit.api.message.TranslatedMessage;
import cn.nukkit.server.Player;
import cn.nukkit.server.command.Command;
import cn.nukkit.server.command.data.CommandParameter;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BanCommand extends VanillaCommand {

    public BanCommand(String name) {
        super(name, "%nukkit.command.ban.player.description", "%commands.ban.usage");
        this.setPermission("nukkit.command.ban.player");
        this.commandParameters.clear();
        this.commandParameters.put("default",
                new CommandParameter[]{
                        new CommandParameter("player", CommandParameter.ARG_TYPE_TARGET, false),
                        new CommandParameter("reason", true)
                });
    }

    @Override
    public boolean execute(CommandExecutorSource sender, String commandLabel, String[] args) {
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

        sender.getServer().getNameBans().addBan(name, reason, null, sender.getName());

        Player player = sender.getServer().getPlayerExact(name);
        if (player != null) {
            player.kick(PlayerKickEvent.Reason.NAME_BANNED, !reason.isEmpty() ? "Banned by admin. Reason: " + reason : "Banned by admin");
        }

        Command.broadcastCommandMessage(sender, new TranslatedMessage("%commands.ban.success", player != null ? player.getName() : name));

        return true;
    }
}
