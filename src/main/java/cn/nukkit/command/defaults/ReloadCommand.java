package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;
import org.jetbrains.annotations.NotNull;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ReloadCommand extends VanillaCommand {

    public ReloadCommand(String name) {
        super(name, "%nukkit.command.reload.description", "%commands.reload.usage");
        this.setPermission("nukkit.command.reload");
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String @NotNull [] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        Command.broadcastCommandMessage(sender, new TranslationContainer(TextFormat.YELLOW + "%nukkit.command.reload.reloading" + TextFormat.WHITE));

        sender.getServer().reload();

        Command.broadcastCommandMessage(sender, new TranslationContainer(TextFormat.YELLOW + "%nukkit.command.reload.reloaded" + TextFormat.WHITE));

        return true;
    }
}
