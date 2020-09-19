package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xtypr
 * @since 2015/12/9
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

        Player player = sender instanceof Player && "@p".equals(args[0])? (Player) sender : sender.getServer().getPlayer(args[0]);
        Item item;

        try {
            item = Item.fromString(args[1]);
        } catch (Exception e) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return true;
        }
        
        if (item instanceof ItemBlock && item.getBlock() instanceof BlockUnknown) {
            sender.sendMessage(new TranslationContainer("commands.give.block.notFound", args[1]));
            return true;
        }

        int count;
        try {
            if (args.length <= 2) {
                count = 1;
            } else {
                count = Integer.parseInt(args[2]);
            }
        } catch (NumberFormatException e) {
            count = 1;
        }
        if (count <= 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return true;
        }
        item.setCount(count);

        if (player == null) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
            return true;
        }
        
        if (item.isNull()) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.give.item.notFound", args[1]));
            return true;
        }
        
        Item[] returns = player.getInventory().addItem(item.clone());
        List<Item> drops = new ArrayList<>();
        for (Item returned: returns) {
            int maxStackSize = returned.getMaxStackSize();
            if (returned.getCount() <= maxStackSize) {
                drops.add(returned);
            } else {
                while (returned.getCount() > maxStackSize) {
                    Item drop = returned.clone();
                    int toDrop = Math.min(returned.getCount(), maxStackSize);
                    drop.setCount(toDrop);
                    returned.setCount(returned.getCount() - toDrop);
                    drops.add(drop);
                }
                if (!returned.isNull()) {
                    drops.add(returned);
                }
            }
        }
        
        for (Item drop: drops) {
            player.dropItem(drop);
        }
        
        Command.broadcastCommandMessage(sender, new TranslationContainer(
                "%commands.give.success",
                item.getName() + " (" + item.getId() + ":" + item.getDamage() + ")",
                String.valueOf(item.getCount()),
                player.getName()));
        return true;
    }
}
