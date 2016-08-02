package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

import java.util.Objects;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class TellCommand extends VanillaCommand {

    public TellCommand(String name) {
        super(name, "%nukkit.command.tell.description", "%commands.message.usage", new String[]{"w", "msg"});
        this.setPermission("nukkit.command.tell");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));

            return false;
        }

        String name = args[0].toLowerCase();

        Player player = sender.getServer().getPlayer(name);
        if (player == null) {
            sender.sendMessage(new TranslationContainer("commands.generic.player.notFound"));
            return true;
        }

        if (Objects.equals(player, sender)) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.message.sameTarget"));
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
