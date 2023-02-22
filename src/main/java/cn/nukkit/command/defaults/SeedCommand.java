package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.lang.TranslationContainer;
import org.jetbrains.annotations.NotNull;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SeedCommand extends VanillaCommand {

    public SeedCommand(String name) {
        super(name, "%nukkit.command.seed.description", "%commands.seed.usage");
        this.setPermission("nukkit.command.seed");
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String @NotNull [] args) {
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
