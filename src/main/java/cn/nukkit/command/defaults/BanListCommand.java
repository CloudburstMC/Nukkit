package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.permission.BanEntry;
import cn.nukkit.permission.BanList;

import java.util.LinkedHashMap;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class BanListCommand extends VanillaCommand {
    public BanListCommand(String name) {
        super(name, "%nukkit.command.banlist.description", "%commands.banlist.usage");
        this.setPermission("nukkit.command.ban.list");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        BanList list;
        if (args.length > 0) {
            args[0] = args[0].toLowerCase();
            if ("ips".equals(args[0])) {
                list = sender.getServer().getIPBans();
            } else if ("players".equals(args[0])) {
                list = sender.getServer().getNameBans();
            } else {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));

                return false;
            }
        } else {
            list = sender.getServer().getNameBans();
            args[0] = "players";
        }

        String message = "";
        LinkedHashMap<String, BanEntry> entries = list.getEntires();
        for (BanEntry entry : entries.values()) {
            message += entry.getName() + ", ";
        }

        if ("ips".equals(args[0])) {
            sender.sendMessage(new TranslationContainer("commands.banlist.ips", String.valueOf(entries.size())));
        } else {
            sender.sendMessage(new TranslationContainer("commands.banlist.players", String.valueOf(entries.size())));
        }

        sender.sendMessage(message.substring(0, message.length() - 2));

        return true;
    }
}
