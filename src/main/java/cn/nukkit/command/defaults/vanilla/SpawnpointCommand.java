package cn.nukkit.command.defaults.vanilla;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.defaults.VanillaCommand;
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
public class SpawnpointCommand extends VanillaCommand {
    public SpawnpointCommand(String name) {
        super(name, "commands.spawnpoint.description", "/spawnpoint [player] <position>");
        this.setPermission("nukkit.command.spawnpoint");

        registerOverload().optionalArg("blockPos", CommandParamType.POSITION);
        registerOverload()
                .requiredArg("target", CommandParamType.TARGET)
                .optionalArg("pos", CommandParamType.POSITION);
    }

    @Override
    public boolean execute(CommandSender sender, String aliasUsed, String[] args) {
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
                    sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                    return true;
                }
                if (y < 0) y = 0;
                if (y > 256) y = 256;
                target.setSpawn(Location.from(x, y, z, level));
                CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("commands.spawnpoint.success.single", target.getName(),
                        NukkitMath.round(x, 2),
                        NukkitMath.round(y, 2),
                        NukkitMath.round(z, 2)));
                return true;
            }
        } else if (args.length <= 1) {
            if (sender instanceof Player) {
                Location pos = ((Player) sender).getLocation();
                target.setSpawn(pos);
                CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("commands.spawnpoint.success.single", target.getName(),
                        NukkitMath.round(pos.getX(), 2),
                        NukkitMath.round(pos.getY(), 2),
                        NukkitMath.round(pos.getZ(), 2)));
                return true;
            } else {
                sender.sendMessage(new TranslationContainer("commands.locate.fail.noplayer"));
                return true;
            }
        }
        sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
        return true;
    }
}
