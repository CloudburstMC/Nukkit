package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.permission.BanEntry;
import cn.nukkit.permission.BanList;
import lombok.extern.log4j.Log4j2;

import java.util.Iterator;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
@Log4j2
public class BanListCommand extends VanillaCommand {
    public BanListCommand(String name) {
        super(name);
        this.setPermission("nukkit.command.ban.list");
        this.setUsage("/banlist <ips|players>");

        registerOverload().literal("ips");
        registerOverload().literal("players");
    }

    @Override
    public boolean execute(CommandSender sender, String aliasUsed, String[] args) {
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
                    sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
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
            sender.sendMessage(new TranslationContainer("commands.banlist.ips", list.getEntires().size()));
        } else {
            sender.sendMessage(new TranslationContainer("commands.banlist.players", list.getEntires().size()));
        }
        sender.sendMessage(builder.toString());
        return true;
    }
}
