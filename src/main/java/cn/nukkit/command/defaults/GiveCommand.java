package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("itemName", CommandEnum.ENUM_ITEM),
                CommandParameter.newType("amount", true, CommandParamType.INT),
                CommandParameter.newType("tags", true, CommandParamType.RAWTEXT)
        });
        this.commandParameters.put("toPlayerById", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newType("itemId", CommandParamType.INT),
                CommandParameter.newType("amount", true, CommandParamType.INT),
                CommandParameter.newType("tags", true, CommandParamType.RAWTEXT)
        });
        this.commandParameters.put("toPlayerByIdMeta", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newType("itemAndData", CommandParamType.STRING),
                CommandParameter.newType("amount", true, CommandParamType.INT),
                CommandParameter.newType("tags", true, CommandParamType.RAWTEXT)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        List<Player> targets = new ArrayList<>();
        if (args[0].equals("@a")) {
            targets.addAll(Server.getInstance().getOnlinePlayers().values());
        }
        else {
            Player target = sender.getServer().getPlayer(args[0].replace("@s", sender.getName()));
            if (target != null) {
                targets.add(target);
            }
            else {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                return false;
            }
        }

        Item item;
        try {
            item = Item.fromString(args[1]);
        } catch (Exception e) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        try {
            item.setCount(Integer.parseInt(args[2]));
        } catch (Exception e) {
            item.setCount(item.getMaxStackSize());
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
