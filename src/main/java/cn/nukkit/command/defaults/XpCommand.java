package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;

/**
 * Created by Snake1999 on 2016/1/22.
 * Package cn.nukkit.command.defaults in project nukkit.
 */
public class XpCommand extends Command {
    public XpCommand() {
        super("xp", CommandData.builder("xp")
                .setDescription("commands.xp.description")
                .setUsageMessage("/xp <amount>[L] [player]")
                .setPermissions("nukkit.command.xp")
                .setParameters(new CommandParameter[]{
                        new CommandParameter("amount|level", CommandParamType.INT, false),
                        new CommandParameter("player", CommandParamType.TARGET, true)
                }).build());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        //  "/xp <amount> [player]"  for adding exp
        //  "/xp <amount>L [player]" for adding exp level
        String amountString;
        String playerName;
        Player player;
        if (!(sender instanceof Player)) {
            if (args.length != 2) {
                return false;
            }
            amountString = args[0];
            playerName = args[1];
            player = sender.getServer().getPlayer(playerName);
        } else {
            if (args.length == 1) {
                amountString = args[0];
                player = (Player) sender;
            } else if (args.length == 2) {
                amountString = args[0];
                playerName = args[1];
                player = sender.getServer().getPlayer(playerName);
            } else {
                return false;
            }
        }

        if (player == null) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
            return true;
        }

        int amount;
        boolean isLevel = false;
        if (amountString.endsWith("l") || amountString.endsWith("L")) {
            amountString = amountString.substring(0, amountString.length() - 1);
            isLevel = true;
        }

        try {
            amount = Integer.parseInt(amountString);
        } catch (NumberFormatException e1) {
            return false;
        }

        if (isLevel) {
            int newLevel = player.getExperienceLevel();
            newLevel += amount;
            if (newLevel > 24791) newLevel = 24791;
            if (newLevel < 0) {
                player.setExperience(0, 0);
            } else {
                player.setExperience(player.getExperience(), newLevel);
            }
            if (amount > 0) {
                sender.sendMessage(new TranslationContainer("commands.xp.success.levels", amount, player.getName()));
            } else {
                sender.sendMessage(new TranslationContainer("commands.xp.success.negative.levels", -amount, player.getName()));
            }
            return true;
        } else {
            if (amount < 0) {
                return false;
            }
            player.addExperience(amount);
            sender.sendMessage(new TranslationContainer("commands.xp.success", amount, player.getName()));
            return true;
        }
    }
}
