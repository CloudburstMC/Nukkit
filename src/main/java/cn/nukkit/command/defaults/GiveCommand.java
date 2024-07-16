package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

import java.util.Collection;
import java.util.Collections;

/**
 * Created on 2015/12/9 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class GiveCommand extends VanillaCommand {

    public GiveCommand(String name) {
        super(name, "%nukkit.command.give.description", "%nukkit.command.give.usage");
        this.setPermission("nukkit.command.give");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false),
                new CommandParameter("itemName", false, CommandParameter.ENUM_TYPE_ITEM_LIST),
                new CommandParameter("amount", CommandParamType.INT, true),
                new CommandParameter("meta", CommandParamType.INT, true),
                new CommandParameter("tags...", CommandParamType.RAWTEXT, true)
        });
        this.commandParameters.put("toPlayerById", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false),
                new CommandParameter("item ID", CommandParamType.INT, false),
                new CommandParameter("amount", CommandParamType.INT, true),
                new CommandParameter("tags...", CommandParamType.RAWTEXT, true)
        });
        this.commandParameters.put("toPlayerByIdMeta", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false),
                new CommandParameter("item ID:meta", CommandParamType.RAWTEXT, false),
                new CommandParameter("amount", CommandParamType.INT, true),
                new CommandParameter("tags...", CommandParamType.RAWTEXT, true)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return true;
        }

        Collection<Player> targets;
        if (args[0].equals("@a")) {
            targets = Server.getInstance().getOnlinePlayers().values();
        } else {
            Player target = sender.getServer().getPlayerExact(args[0].replace("@s", sender.getName()));
            if (target != null) {
                targets = Collections.singletonList(target);
            } else {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                return false;
            }
        }

        Item item;

        try {
            item = Item.fromString(args[1].replace("minecraft:", ""));
        } catch (Exception e) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return true;
        }

        if (item.getDamage() < 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return true;
        }

        try {
            item.setCount(Integer.parseInt(args[2]));
        } catch (Exception e) {
            item.setCount(1);
        }

        if (item.getId() == 0) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.give.item.notFound", args[1]));
            return false;
        }

        for (Player player : targets) {
            player.getInventory().addItem(item.clone());
            Command.broadcastCommandMessage(sender, new TranslationContainer(
                    "%commands.give.success",
                    item.getName() + " (" + item.getId() + ":" + item.getDamage() + ")",
                    String.valueOf(item.getCount()),
                    player.getName()));
        }
        return true;
    }
}
