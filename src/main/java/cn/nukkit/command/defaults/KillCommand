package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.utils.TextFormat;

/**
 * Created on 2015/12/08 by Pub4Game.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class KillCommand extends VanillaCommand {

    public KillCommand(String name) {
        super(name, "%nukkit.command.kill.description", "%nukkit.command.kill.usage");
        this.setPermission("nukkit.command.kill");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        CommandSender target = sender;

        if (args.length > 0) {
            target = sender.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                return true;
            }
        }else if (!(sender instanceof Player)){
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return true;
        }
        if (!(target.equals(sender))) {
            Player player = sender.getServer().getPlayer(args[0]);
            EntityDamageEvent ev = new EntityDamageEvent(player, EntityDamageEvent.CAUSE_SUICIDE, 1000);
            sender.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return true;
            }
            ((Player) target).setLastDamageCause(ev);
            ((Player) target).setHealth(0);
            Command.broadcastCommandMessage(sender, new TranslationContainer("commands.kill.successful", target.getName()));
        }else{
            if (!(sender.hasPermission("nukkit.command.kill.self"))) {
                 sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
                return true;
                }
                Player player = sender.getServer().getPlayer(sender.getName());
                EntityDamageEvent ev = new EntityDamageEvent(player, EntityDamageEvent.CAUSE_SUICIDE, 1000);
                sender.getServer().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return true;
                }
                ((Player) sender).setLastDamageCause(ev);
                ((Player) sender).setHealth(0);
                sender.sendMessage(new TranslationContainer("commands.kill.successful", sender.getName()));
            }
            return true;
        }
    }
