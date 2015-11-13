package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.TranslationContainer;

/**
 * Created on 2015/11/13 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class SaveCommand extends VanillaCommand{

    public SaveCommand(String name) {
        super(name, "%nukkit.command.save.description", "%commands.save.usage");
        this.setPermission("nukkit.command.save.perform");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(!this.testPermission(sender)){
            return true;
        }
        Command.broadcastCommandMessage(sender, new TranslationContainer("commands.save.start"));

        sender.getServer().getOnlinePlayers().forEach((s, p) -> p.save());
        sender.getServer().getLevels().forEach((i, l) -> l.save(true));

        Command.broadcastCommandMessage(sender, new TranslationContainer("commands.save.success"));
        return true;
    }
}
