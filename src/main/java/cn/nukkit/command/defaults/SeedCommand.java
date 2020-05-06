package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SeedCommand extends Command {

    public SeedCommand() {
        super("seed", CommandData.builder("seed")
                .setPermissions("nukkit.command.seed")
                .build());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        long seed;
        if (sender instanceof Player) {
            seed = ((Player) sender).getLevel().getSeed();
        } else {
            seed = sender.getServer().getDefaultLevel().getSeed();
        }

        sender.sendMessage(new TranslationContainer("commands.seed.success", String.valueOf(seed)));

        return true;
    }
}
