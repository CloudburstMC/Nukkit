package cn.nukkit.command.defaults.vanilla;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.defaults.VanillaCommand;
import cn.nukkit.level.Level;
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
        Level level = sender instanceof Player ? ((Player) sender).getLevel() : sender.getServer().getDefaultLevel();

        sender.sendMessage(new TranslationContainer("commands.seed.success", String.valueOf(level.getSeed())));
        return true;
    }
}
