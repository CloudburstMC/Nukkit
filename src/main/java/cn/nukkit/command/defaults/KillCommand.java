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
        this.setPermission("nukkit.command.kill.self;"
                + "nukkit.command.kill.other");
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
            if (args[0].equals("@s")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.ingame"));
                    return true;
                }
                suicide((Player) sender);
                return true;
            }
            if (!sender.hasPermission("nukkit.command.kill.other")) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.unknown", this.getName()));
                return true;
            }
            Player player = sender.getServer().getPlayer(args[0].replace("@s", sender.getName()));
            if (player != null) {
                if (player.isCreative() || player.isSpectator()) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.noTargetMatch"));
                    return true;
                }
                if (!kill(player)) {
                    return true;
                }
                Command.broadcastCommandMessage(sender, new TranslationContainer("commands.kill.successful", player.getName()));
            } else if (args[0].equals("@e")) {
                StringJoiner joiner = new StringJoiner(", ");
                for (Level level : Server.getInstance().getLevels().values()) {
                    for (Entity entity : level.getEntities()) {
                        if (entity instanceof Player) {
                            Player p = (Player) entity;
                            if (p.isCreative() || p.isSpectator()) {
                                continue;
                            }
                        }
                        if (!kill(entity)) {
                            continue;
                        }
                        joiner.add(entity.getName());
                    }
                }
                String entities = joiner.toString();
                if (entities.isEmpty()) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.noTargetMatch"));
                    return true;
                }
                sender.sendMessage(new TranslationContainer("commands.kill.successful", entities));
            } else if (args[0].equals("@a")) {
                StringJoiner joiner = new StringJoiner(", ");
                for (Level level : Server.getInstance().getLevels().values()) {
                    for (Entity entity : level.getEntities()) {
                        if (!(entity instanceof Player)) {
                            continue;
                        }
                        Player p = (Player) entity;
                        if (p.isCreative() || p.isSpectator()) {
                            continue;
                        }
                        if (!kill(entity)) {
                            continue;
                        }
                        joiner.add(entity.getName());
                    }
                }
                String entities = joiner.toString();
                if (entities.isEmpty()) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.noTargetMatch"));
                    return true;
                }
                sender.sendMessage(new TranslationContainer("commands.kill.successful", entities));
            } else {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
            }
            return true;
        }
        if (sender instanceof Player) {
            suicide((Player) sender);
        } else {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }
        return true;
    }

    private void suicide(Player player) {
        if (!player.hasPermission("nukkit.command.kill.self")) {
            player.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.unknown", this.getName()));
            return;
        }
        if (player.isCreative() || player.isSpectator()) {
            player.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.noTargetMatch"));
            return;
        }
        if (!kill(player)) {
            return;
        }
        player.sendMessage(new TranslationContainer("commands.kill.successful", player.getName()));
    }

    private static boolean kill(Entity entity) {
        if (!entity.isAlive()) {
            return false;
        }

        EntityDamageEvent ev = new EntityDamageEvent(entity, DamageCause.SUICIDE, 1000);
        entity.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }
        entity.setLastDamageCause(ev);
        if (entity instanceof Player) {
            entity.setHealth(0);
        } else {
            entity.close();
        }
        return true;
    }
}
