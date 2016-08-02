package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.utils.TextFormat;

/**
 * Created on 2015/12/08 by Pub4Game.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class KillCommand extends VanillaCommand {

    public KillCommand(String name) {
        super(name, "%nukkit.command.kill.description", "%nukkit.command.kill.usage", new String[]{"suicide"});
        this.setPermission("nukkit.command.kill.self;"
                + "nukkit.command.kill.other");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length >= 2) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }
        if (args.length == 1) {
            if (!sender.hasPermission("nukkit.command.kill.other")) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
                return true;
            }
            Player player = sender.getServer().getPlayer(args[0]);
            if (player != null) {
                EntityDamageEvent ev = new EntityDamageEvent(player, EntityDamageEvent.CAUSE_SUICIDE, 1000);
                sender.getServer().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return true;
                }
                player.setLastDamageCause(ev);
                player.setHealth(0);
                Command.broadcastCommandMessage(sender, new TranslationContainer("commands.kill.successful", player.getName()));
            } else {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
            }
            return true;
        }
        if (sender instanceof Player) {
            if (!sender.hasPermission("nukkit.command.kill.self")) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
                return true;
            }
            EntityDamageEvent ev = new EntityDamageEvent((Player) sender, EntityDamageEvent.CAUSE_SUICIDE, 1000);
            sender.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return true;
            }
            ((Player) sender).setLastDamageCause(ev);
            ((Player) sender).setHealth(0);
            sender.sendMessage(new TranslationContainer("commands.kill.successful", sender.getName()));
        } else {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }
        return true;
    }
}
