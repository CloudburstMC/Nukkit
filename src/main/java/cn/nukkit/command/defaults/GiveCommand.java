package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import cn.nukkit.item.RuntimeItemMapping;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

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
                new CommandParameter("itemName", false, "Item"),
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
            item = Item.fromString(args[1]);
        } catch (Exception e) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return true;
        }

        if (item.getDamage() < 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return true;
        }

        int count = 1;
        try {
            count = Integer.parseInt(args[2]);
        } catch (Exception ignore) {
        }
        item.setCount(count);

        if (item.getId() == 0) {
            String identifier = args[1].toLowerCase(Locale.ROOT);
            if (!identifier.contains(":")) {
                identifier = "minecraft:" + identifier;
            }
            RuntimeItemMapping.LegacyEntry entry = RuntimeItems.getMapping().fromIdentifier(identifier);

            if (entry != null) {
                item = Item.get(entry.getLegacyId(), entry.getDamage(), count);
            }
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
