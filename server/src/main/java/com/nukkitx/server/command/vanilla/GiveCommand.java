package com.nukkitx.server.command.vanilla;

import com.nukkitx.api.Player;
import com.nukkitx.api.command.CommandData;
import com.nukkitx.api.command.sender.CommandSender;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.item.ItemTypes;
import com.nukkitx.api.message.TranslationMessage;
import com.nukkitx.server.command.VanillaCommand;
import com.nukkitx.server.item.NukkitItemStackBuilder;

public class GiveCommand extends VanillaCommand {

    public GiveCommand() {
        super("give", CommandData.builder()
                .description("commands.give.description")
                .build());
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender) || args.length <= 1 || !(sender instanceof Player)) {
            return true;
        }

        ItemType type;
        int amount;
        try {
            type = ItemTypes.byId(Integer.parseInt(args[0]));
            amount = Integer.parseInt(args[1]);
        } catch (Exception e) {
            sender.sendMessage(new TranslationMessage("commands.give.item.notFound", args[0]));
            return true;
        }

        if (amount > 0) {
            ItemStack item = new NukkitItemStackBuilder().itemType(type).amount(amount).build();
            ((Player) sender).getInventory().addItem(item);
        }
        return true;
    }
}
