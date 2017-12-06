package cn.nukkit.server.command.defaults;

import cn.nukkit.api.message.TranslatedMessage;
import cn.nukkit.server.Player;

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

        sender.sendMessage(new TranslatedMessage("commands.seed.success", String.valueOf(seed)));

        return true;
    }
}
