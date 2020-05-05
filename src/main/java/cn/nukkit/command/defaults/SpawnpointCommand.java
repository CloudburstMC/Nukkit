package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;

/**
 * Created on 2015/12/13 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class SpawnpointCommand extends Command {
    public SpawnpointCommand() {
        super("spawnpoint", CommandData.builder("spawnpoint")
                .setDescription("commands.spawnpoint.description")
                .setUsageMessage("/spawnpoint [player] <position>")
                .setPermissions("nukkit.command.spawnpoint")
                .setParameters(new CommandParameter[]{
                        new CommandParameter("blockPos", CommandParamType.POSITION, true),
                }, new CommandParameter[]{
                        new CommandParameter("target", CommandParamType.TARGET, false),
                        new CommandParameter("pos", CommandParamType.POSITION, true)
                })
                .build());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        Player target;
        if (args.length == 0) {
            if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage(new TranslationContainer("commands.locate.fail.noplayer"));
                return true;
            }
        } else {
            target = sender.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                return true;
            }
        }
        Level level = target.getLevel();

        if (args.length == 4) {
            if (level != null) {
                int x;
                int y;
                int z;
                try {
                    x = Integer.parseInt(args[1]);
                    y = Integer.parseInt(args[2]);
                    z = Integer.parseInt(args[3]);
                } catch (NumberFormatException e1) {
                    return false;
                }
                if (y < 0) y = 0;
                if (y > 256) y = 256;
                target.setSpawn(Location.from(x, y, z, level));
                CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.spawnpoint.success.single", target.getName(),
                        NukkitMath.round(x, 2),
                        NukkitMath.round(y, 2),
                        NukkitMath.round(z, 2)));
                return true;
            }
        } else if (args.length <= 1) {
            if (sender instanceof Player) {
                Location pos = ((Player) sender).getLocation();
                target.setSpawn(pos);
                CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.spawnpoint.success.single", target.getName(),
                        NukkitMath.round(pos.getX(), 2),
                        NukkitMath.round(pos.getY(), 2),
                        NukkitMath.round(pos.getZ(), 2)));
                return true;
            } else {
                sender.sendMessage(new TranslationContainer("commands.locate.fail.noplayer"));
                return true;
            }
        }
        return false;
    }
}
