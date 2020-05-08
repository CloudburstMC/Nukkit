package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.permission.BanEntry;
import cn.nukkit.permission.BanList;
import lombok.extern.log4j.Log4j2;

import java.util.StringJoiner;

import static cn.nukkit.command.args.builder.LiteralArgumentBuilder.literal;

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

        registerOverload().then(literal("ips"));
        registerOverload().then(literal("players"));
    }

    @Override
    public boolean execute(CommandSender sender, String aliasUsed, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        BanList list = sender.getServer().getNameBans();
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
                    sender.sendMessage(new TranslationContainer("commands.generic.usage", usageMessage));
                    return false;
            }
        }

        sender.sendMessage(new TranslationContainer(ips ? "commands.banlist.ips" : "commands.banlist.players", list.getEntries().size()));
        sender.sendMessage(String.join(", ", list.getEntries().keySet()));
        return true;
    }
}
