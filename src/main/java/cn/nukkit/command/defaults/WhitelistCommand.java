package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.utils.TextFormat;

import java.util.StringJoiner;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class WhitelistCommand extends Command {

    public WhitelistCommand() {
        super("whitelist", CommandData.builder("whitelist")
                .setDescription("commands.whitelist.description")
                .setUsageMessage("/whitelist <on|off|reload|list>\n/whitelist <add|remove> <player>")
                .setPermissions("nukkit.command.whitelist.reload",
                        "nukkit.command.whitelist.enable",
                        "nukkit.command.whitelist.disable",
                        "nukkit.command.whitelist.list",
                        "nukkit.command.whitelist.add",
                        "nukkit.command.whitelist.remove")
                .setParameters(new CommandParameter[]{
                        new CommandParameter("on|off|list|reload", CommandParamType.STRING, false)
                }, new CommandParameter[]{
                        new CommandParameter("add|remove", CommandParamType.STRING, false),
                        new CommandParameter("player", CommandParamType.TARGET, false)
                })
                .build());
    }


    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length == 0 || args.length > 2) {
            return false;
        }

        if (args.length == 1) {
            if (this.badPerm(sender, args[0].toLowerCase())) {
                return false;
            }
            switch (args[0].toLowerCase()) {
                case "reload":
                    sender.getServer().reloadWhitelist();
                    CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.whitelist.reloaded"));

                    return true;
                case "on":
                    sender.getServer().setPropertyBoolean("white-list", true);
                    CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.whitelist.enabled"));

                    return true;
                case "off":
                    sender.getServer().setPropertyBoolean("white-list", false);
                    CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.whitelist.disabled"));

                    return true;
                case "list":
                    StringJoiner result = new StringJoiner(", ");
                    int count = 0;
                    for (String player : sender.getServer().getWhitelist().getAll().keySet()) {
                        result.add(player);
                        ++count;
                    }
                    sender.sendMessage(new TranslationContainer("commands.whitelist.list", count, count));
                    sender.sendMessage(result.toString());

                    return true;
                case "add":
                case "remove":
                    return false;
            }
        } else {
            if (this.badPerm(sender, args[0].toLowerCase())) {
                return false;
            }
            switch (args[0].toLowerCase()) {
                case "add":
                    sender.getServer().getOfflinePlayer(args[1]).setWhitelisted(true);
                    CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.whitelist.add.success", args[1]));

                    return true;
                case "remove":
                    sender.getServer().getOfflinePlayer(args[1]).setWhitelisted(false);
                    CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.whitelist.remove.success", args[1]));

                    return true;
            }
        }

        return true;
    }

    private boolean badPerm(CommandSender sender, String perm) {
        if (!sender.hasPermission("nukkit.command.whitelist" + perm)) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));

            return true;
        }

        return false;
    }
}
