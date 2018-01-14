package cn.nukkit.server.command.defaults;

import cn.nukkit.api.message.TranslatedMessage;
import cn.nukkit.server.Player;
import cn.nukkit.server.command.Command;
import cn.nukkit.server.command.data.CommandParameter;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.level.Position;
import cn.nukkit.server.util.TextFormat;

import java.text.DecimalFormat;

/**
 * Created on 2015/12/13 by xtypr.
 * Package cn.nukkit.server.command.defaults in project Nukkit .
 */
public class SpawnpointCommand extends VanillaCommand {
    public SpawnpointCommand(String name) {
        super(name, "%nukkit.command.spawnpoint.description", "%commands.spawnpoint.usage");
        this.setPermission("nukkit.command.spawnpoint");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("blockPos", CommandParameter.ARG_TYPE_BLOCK_POS, true),
        });
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
                sender.sendMessage(new TranslatedMessage("commands.generic.ingame"));
                return true;
            }
        } else {
            target = sender.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(new TranslatedMessage(TextFormat.RED + "%commands.generic.player.notFound"));
                return true;
            }
        }
        NukkitLevel level = target.getLevel();
        DecimalFormat round2 = new DecimalFormat("##0.00");
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
                    sender.sendMessage(new TranslatedMessage("commands.generic.usage", this.usageMessage));
                    return true;
                }
                if (y < 0) y = 0;
                if (y > 256) y = 256;
                target.setSpawn(new Position(x, y, z, level));
                Command.broadcastCommandMessage(sender, new TranslatedMessage("commands.spawnpoint.success", new String[]{
                        target.getName(),
                        round2.format(x),
                        round2.format(y),
                        round2.format(z)
                }));
                return true;
            }
        } else if (args.length <= 1) {
            if (sender instanceof Player) {
                Position pos = (Position) sender;
                target.setSpawn(pos);
                Command.broadcastCommandMessage(sender, new TranslatedMessage("commands.spawnpoint.success", new String[]{
                        target.getName(),
                        round2.format(pos.x),
                        round2.format(pos.y),
                        round2.format(pos.z)
                }));
                return true;
            } else {
                sender.sendMessage(new TranslatedMessage("commands.generic.ingame"));
                return true;
            }
        }
        sender.sendMessage(new TranslatedMessage("commands.generic.usage", this.usageMessage));
        return true;
    }
}
