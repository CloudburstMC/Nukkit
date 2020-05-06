package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;
import lombok.extern.log4j.Log4j2;

import static cn.nukkit.block.BlockIds.AIR;

/**
 * Created on 2015/12/9 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
@Log4j2
public class GiveCommand extends Command {
    public GiveCommand() {
        super("give", CommandData.builder("give")
                .setDescription("commands.give.description")
                .setUsageMessage("commands.give.usage")
                .setPermissions("nukkit.command.give")
                .setParameters(new CommandParameter[]{
                        new CommandParameter("player", CommandParamType.TARGET, false),
                        new CommandParameter("itemName", false, CommandParameter.ENUM_TYPE_ITEM_LIST),
                        new CommandParameter("amount", CommandParamType.INT, true),
                        new CommandParameter("meta", CommandParamType.INT, true),
                        new CommandParameter("tags...", CommandParamType.RAWTEXT, true)
                }, new CommandParameter[]{
                        new CommandParameter("player", CommandParamType.TARGET, false),
                        new CommandParameter("item ID", CommandParamType.INT, false),
                        new CommandParameter("amount", CommandParamType.INT, true),
                        new CommandParameter("tags...", CommandParamType.RAWTEXT, true)
                }, new CommandParameter[]{
                        new CommandParameter("player", CommandParamType.TARGET, false),
                        new CommandParameter("item ID:meta", CommandParamType.RAWTEXT, false),
                        new CommandParameter("amount", CommandParamType.INT, true),
                        new CommandParameter("tags...", CommandParamType.RAWTEXT, true)
                })
                .build());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length < 2) {
            return false;
        }

        Player player = sender.getServer().getPlayer(args[0]);
        Item item;

        try {
            item = Item.fromString(args[1]);
        } catch (Exception e) {
            log.throwing(e);
            return false;
        }

        try {
            item.setCount(Integer.parseInt(args[2]));
        } catch (Exception e) {
            item.setCount(item.getMaxStackSize());
        }

        if (player != null) {
            if (item.getId() == AIR) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.give.item.invalid", args[1]));
                return true;
            }
            player.getInventory().addItem(item.clone());
        } else {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));

            return true;
        }
        CommandUtils.broadcastCommandMessage(sender, new TranslationContainer(
                "%commands.give.success",
                item.getName() + " (" + item.getId() + ":" + item.getMeta() + ")",
                item.getCount(),
                player.getName()));
        return true;
    }
}
