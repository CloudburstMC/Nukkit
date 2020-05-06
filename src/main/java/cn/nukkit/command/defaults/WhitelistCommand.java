package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.utils.TextFormat;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class WhitelistCommand extends VanillaCommand {

    public WhitelistCommand(String name) {
        super(name, "commands.whitelist.description", "/whitelist <on|off|reload|list>\n/whitelist <add|remove> <player>");
        this.setPermission(
                "nukkit.command.whitelist.reload;" +
                        "nukkit.command.whitelist.enable;" +
                        "nukkit.command.whitelist.disable;" +
                        "nukkit.command.whitelist.list;" +
                        "nukkit.command.whitelist.add;" +
                        "nukkit.command.whitelist.remove"
        );

//        registerOverload()
//                .literals("action", "WhitelistAction", new String[]{"on", "off", "list", "reload", "add", "remove"})
//                .optionalArg("name", CommandParamType.STRING);
    }


    @Override
    public boolean execute(CommandSender sender, String aliasUsed, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length == 0 || args.length > 2) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return true;
        }

        if (args.length == 1) {
            if (this.badPerm(sender, args[0].toLowerCase())) {
                return false;
            }
            switch (args[0].toLowerCase()) {
                case "reload":
                    sender.getServer().reloadWhitelist();
                    CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("commands.whitelist.reloaded"));

                    return true;
                case "on":
                    sender.getServer().setPropertyBoolean("white-list", true);
                    CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("commands.whitelist.enabled"));

                    return true;
                case "off":
                    sender.getServer().setPropertyBoolean("white-list", false);
                    CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("commands.whitelist.disabled"));

                    return true;
                case "list":
                    String result = "";
                    int count = 0;
                    for (String player : sender.getServer().getWhitelist().getAll().keySet()) {
                        result += player + ", ";
                        ++count;
                    }
                    sender.sendMessage(new TranslationContainer("commands.whitelist.list", count, count));
                    sender.sendMessage(result.length() > 0 ? result.substring(0, result.length() - 2) : "");

                    return true;

                case "add":
                case "remove":
                    sender.sendMessage(new TranslationContainer("commands.generic.usage", usageMessage));
                    return true;
            }
        } else if (args.length == 2) {
            if (this.badPerm(sender, args[0].toLowerCase())) {
                return false;
            }
            switch (args[0].toLowerCase()) {
                case "add":
                    sender.getServer().getOfflinePlayer(args[1]).setWhitelisted(true);
                    CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("commands.whitelist.add.success", args[1]));

                    return true;
                case "remove":
                    sender.getServer().getOfflinePlayer(args[1]).setWhitelisted(false);
                    CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("commands.whitelist.remove.success", args[1]));

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
