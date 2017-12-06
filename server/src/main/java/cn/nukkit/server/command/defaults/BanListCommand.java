package cn.nukkit.server.command.defaults;

import cn.nukkit.api.message.TranslatedMessage;
import cn.nukkit.server.command.data.CommandParameter;
import cn.nukkit.server.permission.BanEntry;
import cn.nukkit.server.permission.BanList;

import java.util.Iterator;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.server.command.defaults in project Nukkit .
 */
public class BanListCommand extends VanillaCommand {
    public BanListCommand(String name) {
        super(name, "%nukkit.command.banlist.description", "%commands.banlist.usage");
        this.setPermission("nukkit.command.ban.list");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("ips|players", true)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        BanList list;
        boolean ips = false;
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "ips":
                    list = sender.getServer().getIPBans();
                    ips = true;
                    break;
                case "players":
                    list = sender.getServer().getNameBans();
                    break;
                default:
                    sender.sendMessage(new TranslatedMessage("commands.generic.usage", this.usageMessage));
                    return false;
            }
        } else {
            list = sender.getServer().getNameBans();
        }

        StringBuilder builder = new StringBuilder();
        Iterator<BanEntry> itr = list.getEntires().values().iterator();
        while (itr.hasNext()) {
            builder.append(itr.next().getName());
            if (itr.hasNext()) {
                builder.append(", ");
            }
        }

        if (ips) {
            sender.sendMessage(new TranslatedMessage("commands.banlist.ips", String.valueOf(list.getEntires().size())));
        } else {
            sender.sendMessage(new TranslatedMessage("commands.banlist.players", String.valueOf(list.getEntires().size())));
        }
        sender.sendMessage(builder.toString());
        return true;
    }
}
