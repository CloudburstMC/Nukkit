package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.lang.TranslationContainer;
import org.jetbrains.annotations.NotNull;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class StopCommand extends VanillaCommand {

    public StopCommand(String name) {
        super(name, "%nukkit.command.stop.description", "%commands.stop.usage");
        this.setPermission("nukkit.command.stop");
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String @NotNull [] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        Command.broadcastCommandMessage(sender, new TranslationContainer("commands.stop.start"));

        sender.getServer().shutdown();

        return true;
    }
}
