package cn.nukkit.command.defaults.vanilla;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.defaults.VanillaCommand;
import cn.nukkit.item.Item;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;
import lombok.extern.log4j.Log4j2;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.command.args.builder.OptionalArgumentBuilder.optionalArg;
import static cn.nukkit.command.args.builder.RequiredArgumentBuilder.requiredArg;

/**
 * Created on 2015/12/9 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
@Log4j2
public class GiveCommand extends VanillaCommand {
    public GiveCommand(String name) {
        super(name, "commands.give.description", "commands.give.usage");
        this.setPermission("nukkit.command.give");

        registerOverload()
                .then(requiredArg("player", CommandParamType.TARGET))
                .then(requiredArg("itemName", CommandParameter.ENUM_TYPE_ITEM_LIST))
                .then(optionalArg("amount", CommandParamType.INT))
                .then(optionalArg("data", CommandParamType.INT)); // TODO: components
    }

    @Override
    public boolean execute(CommandSender sender, String aliasUsed, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));

            return true;
        }

        Player player = sender.getServer().getPlayer(args[0]);
        Item item;

        try {
            item = Item.fromString(args[1]);
        } catch (Exception e) {
            log.throwing(e);
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return true;
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
