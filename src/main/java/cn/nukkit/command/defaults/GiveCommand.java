package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created on 2015/12/9 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class GiveCommand extends VanillaCommand {
    public GiveCommand(String name) {
        super(name, "%nukkit.command.give.description", "%nukkit.command.give.usage");
        this.setPermission("nukkit.command.give");
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
        Player player = sender.getServer().getPlayer(args[0]);
        Item rawItem = Item.fromString(args[1]);
        Item[] items;
        if (!(args.length >= 3)) {
            rawItem.setCount(rawItem.getMaxStackSize());
            items = new Item[]{rawItem};
        } else if (!args[2].matches("^[1-9]+\\d*$")) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return true;
        } else {
            Collection<Item> itemsCollection = new ArrayList<>();
            int count = Integer.parseInt(args[2]);

            while (count > 0) {
                Item slot = rawItem.clone();
                if (count > rawItem.getMaxStackSize()) {
                    itemsCollection.add(slot);
                } else {
                    slot.setCount(count);
                    itemsCollection.add(slot);
                }
                count -= rawItem.getMaxStackSize();
            }
            items = itemsCollection.toArray(new Item[1]);
        }
        if (player != null) {
            if (rawItem.getId() == 0) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.give.item.notFound", args[1]));
                return true;
            }
            player.getInventory().addItem(items);
        } else {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
            return true;
        }
        Command.broadcastCommandMessage(sender, new TranslationContainer(
                "%commands.give.success",
                new String[]{
                        rawItem.getName() + " (" + rawItem.getId() + ":" + rawItem.getDamage() + ")",
                        args[2],
                        player.getName()
                }
        ));
        return true;
    }
}
