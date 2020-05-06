package cn.nukkit.command.defaults.vanilla;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.defaults.VanillaCommand;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SeedCommand extends VanillaCommand {

    public SeedCommand(String name) {
        super(name);
        this.setPermission("nukkit.command.seed");
        this.setUsage("/seed");
    }

    @Override
    public boolean execute(CommandSender sender, String aliasUsed, String[] args) {
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
