package cn.nukkit.command.defaults.vanilla;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.defaults.VanillaCommand;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.command.args.builder.OptionalArgumentBuilder.optionalArg;
import static cn.nukkit.command.args.builder.RequiredArgumentBuilder.requiredArg;

/**
 * Created by Pub4Game on 23.01.2016.
 */
public class EnchantCommand extends VanillaCommand {

    public EnchantCommand(String name) {
        super(name, "commands.enchant.description", "/enchant <player> <enchant ID> [level]");
        this.setPermission("nukkit.command.enchant");

        registerOverload()
                .then(requiredArg("player", CommandParamType.TARGET))
                .then(requiredArg("enchantment ID", CommandParamType.INT))
                .then(optionalArg("level", CommandParamType.INT));

        registerOverload()
                .then(requiredArg("player", CommandParamType.TARGET))
                .then(requiredArg("id", CommandParameter.ENUM_TYPE_ENCHANTMENT_LIST))
                .then(optionalArg("level", CommandParamType.INT));
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
        if (player == null) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
            return true;
        }
        int enchantId;
        int enchantLevel;
        try {
            enchantId = getIdByName(args[1]);
            enchantLevel = args.length == 3 ? Integer.parseInt(args[2]) : 1;
        } catch (NumberFormatException e) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return true;
        }
        Enchantment enchantment = Enchantment.getEnchantment(enchantId);
        if (enchantment == null) {
            sender.sendMessage(new TranslationContainer("commands.enchant.notFound", String.valueOf(enchantId)));
            return true;
        }
        enchantment.setLevel(enchantLevel);
        Item item = player.getInventory().getItemInHand();
        if (item.getId() == AIR) {
            sender.sendMessage(new TranslationContainer("commands.enchant.noItem"));
            return true;
        }
        item.addEnchantment(enchantment);
        player.getInventory().setItemInHand(item);
        CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.enchant.success"));
        return true;
    }

    public int getIdByName(String value) throws NumberFormatException {
        switch (value) {
            case "protection":
                return 0;
            case "fire_protection":
                return 1;
            case "feather_falling":
                return 2;
            case "blast_protection":
                return 3;
            case "projectile_projection":
                return 4;
            case "thorns":
                return 5;
            case "respiration":
                return 6;
            case "aqua_affinity":
                return 7;
            case "depth_strider":
                return 8;
            case "sharpness":
                return 9;
            case "smite":
                return 10;
            case "bane_of_arthropods":
                return 11;
            case "knockback":
                return 12;
            case "fire_aspect":
                return 13;
            case "looting":
                return 14;
            case "efficiency":
                return 15;
            case "silk_touch":
                return 16;
            case "durability":
                return 17;
            case "fortune":
                return 18;
            case "power":
                return 19;
            case "punch":
                return 20;
            case "flame":
                return 21;
            case "infinity":
                return 22;
            case "luck_of_the_sea":
                return 23;
            case "lure":
                return 24;
            case "frost_walker":
                return 25;
            case "mending":
                return 26;
            case "binding_curse":
                return 27;
            case "vanishing_curse":
                return 28;
            case "impaling":
                return 29;
            case "loyality":
                return 30;
            case "riptide":
                return 31;
            case "channeling":
                return 32;
            default:
                return Integer.parseInt(value);
        }
    }
}
