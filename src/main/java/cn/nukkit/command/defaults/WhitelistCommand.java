package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.utils.TextFormat;

import java.util.Objects;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class WhitelistCommand extends VanillaCommand {

    public WhitelistCommand(String name) {
        super(name, "%nukkit.command.whitelist.description", "%commands.whitelist.usage");
        this.setPermission(
                "nukkit.command.whitelist.reload;" +
                        "nukkit.command.whitelist.enable;" +
                        "nukkit.command.whitelist.disable;" +
                        "nukkit.command.whitelist.list;" +
                        "nukkit.command.whitelist.add;" +
                        "nukkit.command.whitelist.remove"
        );
    }

    private static class WhitelistCommandRequest {
        enum RequestType {RELOAD, ON, OFF, LIST, ADD, REMOVE, INVALID}

        private RequestType requestType;

        RequestType getRequestType() {
            return requestType;
        }

        private String playerName;

        String getPlayerName() {
            return playerName;
        }

        private WhitelistCommandRequest(RequestType type, String p) {
            requestType = type;
            playerName = p;
        }

        private WhitelistCommandRequest(RequestType type) {
            this(type, null);
        }

        static WhitelistCommandRequest of(String[] args) {
            if (args.length == 1 && Objects.equals(args[0], "on")) {
                return new WhitelistCommandRequest(RequestType.ON);
            } else if (args.length == 1 && Objects.equals(args[0], "off")) {
                return new WhitelistCommandRequest(RequestType.OFF);
            } else if (args.length == 1 && Objects.equals(args[0], "reload")) {
                return new WhitelistCommandRequest(RequestType.RELOAD);
            } else if (args.length == 1 && Objects.equals(args[0], "list")) {
                return new WhitelistCommandRequest(RequestType.LIST);
            } else if (args.length == 2 && Objects.equals(args[0], "add")) {
                return new WhitelistCommandRequest(RequestType.ADD, args[1]);
            } else if (args.length == 2 && Objects.equals(args[0], "remove")) {
                return new WhitelistCommandRequest(RequestType.REMOVE, args[1]);
            } else {
                return new WhitelistCommandRequest(RequestType.INVALID);
            }
        }

        boolean testPerm(CommandSender sender) {
            String permString;
            switch (getRequestType()) {
                case ON:
                    permString = "nukkit.command.whitelist.enable";
                    break;
                case OFF:
                    permString = "nukkit.command.whitelist.disable";
                    break;
                case RELOAD:
                    permString = "nukkit.command.whitelist.reload";
                    break;
                case LIST:
                    permString = "nukkit.command.whitelist.list";
                    break;
                case ADD:
                    permString = "nukkit.command.whitelist.add";
                    break;
                case REMOVE:
                    permString = "nukkit.command.whitelist.remove";
                    break;
                case INVALID:
                default:
                    return true; //虽然“有权限”，但是下面的switch里面会归类到invalid
            }
            return sender.hasPermission(permString);
        }
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        WhitelistCommandRequest request = WhitelistCommandRequest.of(args);
        if (!request.testPerm(sender)) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
            return false;
        }
        switch (request.getRequestType()) {
            case ON:
                sender.getServer().setPropertyBoolean("white-list", true);
                Command.broadcastCommandMessage(sender, new TranslationContainer("commands.whitelist.enabled"));
                break;
            case OFF:
                sender.getServer().setPropertyBoolean("white-list", false);
                Command.broadcastCommandMessage(sender, new TranslationContainer("commands.whitelist.disabled"));
                break;
            case RELOAD:
                sender.getServer().reloadWhitelist();
                Command.broadcastCommandMessage(sender, new TranslationContainer("commands.whitelist.reloaded"));
                break;
            case LIST:
                final String[] result = {""};
                final int[] count = {0};
                sender.getServer().getWhitelist().getAll().forEach((s, p) -> {
                    result[0] += s + ", ";
                    ++count[0];
                });
                String countString = String.valueOf(count[0]);
                sender.sendMessage(new TranslationContainer("commands.whitelist.list", new String[]{countString, countString}));
                sender.sendMessage(result[0]);
                break;
            case ADD:
                sender.getServer().getOfflinePlayer(request.getPlayerName()).setWhitelisted(true);
                Command.broadcastCommandMessage(sender,
                        new TranslationContainer("commands.whitelist.add.success", new String[]{request.getPlayerName()})
                );
                break;
            case REMOVE:
                sender.getServer().getOfflinePlayer(request.getPlayerName()).setWhitelisted(false);
                Command.broadcastCommandMessage(sender,
                        new TranslationContainer("commands.whitelist.remove.success", new String[]{request.getPlayerName()})
                );
                break;
            case INVALID:
            default:
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
        }
        return true;
    }
}
