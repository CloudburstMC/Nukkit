package cn.nukkit.server.command.defaults;

import cn.nukkit.api.message.TranslatedMessage;
import cn.nukkit.server.Player;
import cn.nukkit.server.command.data.CommandParameter;
import cn.nukkit.server.utils.TextFormat;

import java.util.Objects;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.server.command.defaults in project Nukkit .
 */
public class TellCommand extends VanillaCommand {

    public TellCommand(String name) {
        super(name, "%nukkit.command.tell.description", "%commands.message.usage", new String[]{"w", "msg"});
        this.setPermission("nukkit.command.tell");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParameter.ARG_TYPE_TARGET, false),
                new CommandParameter("message")
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(new TranslatedMessage("commands.generic.usage", this.usageMessage));

            return false;
        }

        String name = args[0].toLowerCase();

        Player player = sender.getServer().getPlayer(name);
        if (player == null) {
            sender.sendMessage(new TranslatedMessage("commands.generic.player.notFound"));
            return true;
        }

        if (Objects.equals(player, sender)) {
            sender.sendMessage(new TranslatedMessage(TextFormat.RED + "%commands.message.sameTarget"));
            return true;
        }

        String msg = "";
        for (int i = 1; i < args.length; i++) {
            msg += args[i] + " ";
        }
        if (msg.length() > 0) {
            msg = msg.substring(0, msg.length() - 1);
        }

        String displayName = (sender instanceof Player ? ((Player) sender).getDisplayName() : sender.getName());

        sender.sendMessage("[" + sender.getName() + " -> " + player.getDisplayName() + "] " + msg);
        player.sendMessage("[" + displayName + " -> " + player.getName() + "] " + msg);

        return true;
    }
}
