package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityPotionEffectEvent;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.utils.TextFormat;

import java.util.StringJoiner;

/**
 * Created on 2015/12/08 by Pub4Game.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class KillCommand extends VanillaCommand {

    public KillCommand(String name) {
        super(name, "%nukkit.command.kill.description", "%nukkit.command.kill.usage", new String[]{"suicide"});
        this.setPermission("nukkit.command.kill.self;nukkit.command.kill.other");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", true, CommandParamType.TARGET)
        });
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
            Player player = sender.getServer().getPlayerExact(args[0]);
            if (player != null) {
                if (player.isCreative() || player.isSpectator()) {
                    sender.sendMessage(TextFormat.RED + "No targets matched selector");
                    return true;
                }
                if (resetHealth(player)) {
                    Command.broadcastCommandMessage(sender, new TranslationContainer("commands.kill.successful", player.getName()));
                }
            } else if (args[0].equals("@e")) {
                StringJoiner joiner = new StringJoiner(", ");
                for (Level level : Server.getInstance().getLevels().values()) {
                    for (Entity entity : level.getEntities()) {
                        if (!(entity instanceof Player)) {
                            EntityDamageEvent ev = new EntityDamageEvent(entity, DamageCause.SUICIDE, 1000);
                            sender.getServer().getPluginManager().callEvent(ev);
                            if (ev.isCancelled()) {
                                continue;
                            }
                            joiner.add(entity.getName());
                            entity.setLastDamageCause(ev);
                            entity.close();
                        }
                    }
                }
                String entities = joiner.toString();
                sender.sendMessage(new TranslationContainer("commands.kill.successful", entities.isEmpty() ? "0" : entities));
            } else if (args[0].equals("@s")) {
                if (!sender.hasPermission("nukkit.command.kill.self")) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(new TranslationContainer("%commands.generic.ingame"));
                    return true;
                }
                Player p = (Player) sender;
                if (p.isCreative() || p.isSpectator()) {
                    sender.sendMessage(TextFormat.RED + "No targets matched selector");
                    return true;
                }
                if (resetHealth(p)) {
                    sender.sendMessage(new TranslationContainer("commands.kill.successful", sender.getName()));
                }
            } else if (args[0].equals("@a")) {
                if (!sender.hasPermission("nukkit.command.kill.other")) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
                    return true;
                }
                StringJoiner joiner = new StringJoiner(", ");
                for (Level level : Server.getInstance().getLevels().values()) {
                    for (Entity entity : level.getEntities()) {
                        if (entity instanceof Player) {
                            Player p = (Player) entity;
                            if (p.isCreative() || p.isSpectator()) {
                                continue;
                            }
                            resetHealth(p);

                            joiner.add(entity.getName());
                        }
                    }
                }
                String entities = joiner.toString();
                sender.sendMessage(new TranslationContainer("commands.kill.successful", entities.isEmpty() ? "0" : entities));
            } else {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
            }
            return true;
        } else if (sender instanceof Player) {
            if (!sender.hasPermission("nukkit.command.kill.self")) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
                return true;
            }
            Player player = (Player) sender;
            if (player.isCreative() || player.isSpectator()) {
                sender.sendMessage(TextFormat.RED + "No targets matched selector");
                return true;
            }
            if (resetHealth(player)) {
                sender.sendMessage(new TranslationContainer("commands.kill.successful", sender.getName()));
            }
        } else {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
        }
        return true;
    }

    private boolean resetHealth(Entity entity) {
        EntityDamageEvent ev = new EntityDamageEvent(entity, DamageCause.SUICIDE, 1000);
        entity.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }
        entity.setLastDamageCause(ev);
        entity.removeAllEffects(EntityPotionEffectEvent.Cause.DEATH); // Fix issue with absorption
        entity.setHealth(0);
        return true;
    }
}
