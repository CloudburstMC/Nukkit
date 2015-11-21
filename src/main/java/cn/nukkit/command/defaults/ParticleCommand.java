package cn.nukkit.command.defaults;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.utils.TextFormat;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class ParticleCommand extends VanillaCommand {
    public ParticleCommand(String name) {
        super(name, "%nukkit.command.deop.description", "%commands.deop.usage");
        this.setPermission("nukkit.command.op.take");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", new String[]{this.usageMessage}));
            return false;
        }
        String playerName = args[0];
        IPlayer player = sender.getServer().getOfflinePlayer(playerName);
        player.setOp(false);

        Command.broadcastCommandMessage(sender, new TranslationContainer("commands.deop.success", new String[]{player.getName()}));
        if (player instanceof Player)
            ((Player) player).sendMessage(TextFormat.GRAY + "You are no longer op!"); //todo use TranslationContainer?
        return true;
    }
}
