package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.lang.TranslationContainer;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class BanCommand extends VanillaCommand {

    public BanCommand(String name) {
        super(name, "%nukkit.command.ban.player.description", "%commands.ban.usage");
        this.setPermission("nukkit.command.ban.player");
        this.commandParameters.clear();
        this.commandParameters.put("default",
                new CommandParameter[]{
                        CommandParameter.newType("player", false, CommandParamType.TARGET),
                        CommandParameter.newType("reason", true, CommandParamType.STRING)
                });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));

            return false;
        }

        String name = args[0].replace("@s", sender.getName());
        StringBuilder reason = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reason.append(args[i]).append(' ');
        }

        if (reason.length() > 0) {
            reason = new StringBuilder(reason.substring(0, reason.length() - 1));
        }

        sender.getServer().getNameBans().addBan(name, reason.toString(), null, sender.getName());

        Player player = sender.getServer().getPlayerExact(name);
        if (player != null) {
            player.kick(PlayerKickEvent.Reason.NAME_BANNED, (reason.length() > 0) ? "You are banned! Reason: " + reason : "You are banned!", true);
        }

        Command.broadcastCommandMessage(sender, new TranslationContainer("%commands.ban.success", player != null ? player.getName() : name));

        return true;
    }
}
