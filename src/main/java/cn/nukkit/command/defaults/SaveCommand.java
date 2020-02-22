package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;

/**
 * Created on 2015/11/13 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class SaveCommand extends VanillaCommand {

    public SaveCommand(String name) {
        super(name, "%commands.save.description", "%commands.save.usage");
        this.setPermission("nukkit.command.save.perform");
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        Command.broadcastCommandMessage(sender, new TranslationContainer("commands.save.start"));

        for (Player player : sender.getServer().getOnlinePlayers().values()) {
            player.save();
        }

        for (Level level : sender.getServer().getLevels()) {
            level.save(true);
        }

        Command.broadcastCommandMessage(sender, new TranslationContainer("commands.save.success"));
        return true;
    }
}
