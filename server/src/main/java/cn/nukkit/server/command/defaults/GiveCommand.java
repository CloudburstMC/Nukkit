package cn.nukkit.server.command.defaults;

import cn.nukkit.api.message.TranslatedMessage;
import cn.nukkit.server.Player;
import cn.nukkit.server.command.Command;
import cn.nukkit.server.command.data.CommandParameter;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.utils.TextFormat;

/**
 * Created on 2015/12/9 by xtypr.
 * Package cn.nukkit.server.command.defaults in project Nukkit .
 */
public class GiveCommand extends VanillaCommand {
    public GiveCommand(String name) {
        super(name, "%nukkit.command.give.description", "%nukkit.command.give.usage");
        this.setPermission("nukkit.command.give");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParameter.ARG_TYPE_TARGET, false),
                new CommandParameter("item", false, CommandParameter.ENUM_TYPE_ITEM_LIST),
                new CommandParameter("amount", CommandParameter.ARG_TYPE_INT, true),
                new CommandParameter("meta", CommandParameter.ARG_TYPE_INT, true),
                new CommandParameter("tags...", CommandParameter.ARG_TYPE_RAW_TEXT, true)
        });
        this.commandParameters.put("toPlayerById", new CommandParameter[]{
                new CommandParameter("player", CommandParameter.ARG_TYPE_TARGET, false),
                new CommandParameter("item ID", CommandParameter.ARG_TYPE_INT, false),
                new CommandParameter("amount", CommandParameter.ARG_TYPE_INT, true),
                new CommandParameter("tags...", CommandParameter.ARG_TYPE_RAW_TEXT, true)
        });
        this.commandParameters.put("toPlayerByIdMeta", new CommandParameter[]{
                new CommandParameter("player", CommandParameter.ARG_TYPE_TARGET, false),
                new CommandParameter("item ID:meta", CommandParameter.ARG_TYPE_RAW_TEXT, false),
                new CommandParameter("amount", CommandParameter.ARG_TYPE_INT, true),
                new CommandParameter("tags...", CommandParameter.ARG_TYPE_RAW_TEXT, true)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(new TranslatedMessage("commands.generic.usage", this.usageMessage));

            return true;
        }

        Player player = sender.getServer().getPlayer(args[0]);
        Item item;

        try {
            item = Item.fromString(args[1]);
        } catch (Exception e) {
            sender.sendMessage(new TranslatedMessage("commands.generic.usage", this.usageMessage));
            return true;
        }

        try {
            item.setCount(Integer.parseInt(args[2]));
        } catch (Exception e) {
            item.setCount(item.getMaxStackSize());
        }

        if (player != null) {
            if (item.getId() == 0) {
                sender.sendMessage(new TranslatedMessage(TextFormat.RED + "%commands.give.item.notFound", args[1]));
                return true;
            }
            player.getInventory().addItem(item.clone());
        } else {
            sender.sendMessage(new TranslatedMessage(TextFormat.RED + "%commands.generic.player.notFound"));

            return true;
        }
        Command.broadcastCommandMessage(sender, new TranslatedMessage(
                "%commands.give.success",
                new String[]{
                        item.getName() + " (" + item.getId() + ":" + item.getDamage() + ")",
                        String.valueOf(item.getCount()),
                        player.getName()
                }
        ));
        return true;
    }
}
