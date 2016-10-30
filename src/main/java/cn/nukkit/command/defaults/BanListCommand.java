package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
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
        this.commandParameters = new CommandParameter[]{
                new CommandParameter("ips|players", true)
        };
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        BanList list;
        String arg;
        if (args.length > 0) {
            arg = args[0].toLowerCase();
            if ("ips".equals(arg)) {
                list = sender.getServer().getIPBans();
            } else if ("players".equals(arg)) {
                list = sender.getServer().getNameBans();
            } else {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));

                return false;
            }
        } else {
            list = sender.getServer().getNameBans();
            arg = "players";
        }

        String message = "";
        LinkedHashMap<String, BanEntry> entries = list.getEntires();
        for (BanEntry entry : entries.values()) {
            message += entry.getName() + ", ";
        }

        if ("ips".equals(arg)) {
            sender.sendMessage(new TranslationContainer("commands.banlist.ips", String.valueOf(entries.size())));
        } else {
            sender.sendMessage(new TranslationContainer("commands.banlist.players", String.valueOf(entries.size())));
        }

        if (message.length() > 0) {
            message = message.substring(0, message.length() - 2);
        }
        sender.sendMessage(message);

        return true;
    }
}
