package cn.nukkit.server.command.defaults;

import cn.nukkit.api.message.TranslatedMessage;
import cn.nukkit.server.Player;
import cn.nukkit.server.command.Command;
import cn.nukkit.server.command.data.CommandParameter;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.math.Vector3;

import java.text.DecimalFormat;

/**
 * Created on 2015/12/13 by xtypr.
 * Package cn.nukkit.server.command.defaults in project Nukkit .
 */
public class SetWorldSpawnCommand extends VanillaCommand {
    public SetWorldSpawnCommand(String name) {
        super(name, "%nukkit.command.setworldspawn.description", "%commands.setworldspawn.usage");
        this.setPermission("nukkit.command.setworldspawn");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("blockPos", CommandParameter.ARG_TYPE_BLOCK_POS, true)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        NukkitLevel level;
        Vector3 pos;
        if (args.length == 0) {
            if (sender instanceof Player) {
                level = ((Player) sender).getLevel();
                pos = ((Player) sender).round();
            } else {
                sender.sendMessage(new TranslatedMessage("commands.generic.ingame"));
                return true;
            }
        } else if (args.length == 3) {
            level = sender.getServer().getDefaultLevel();
            try {
                pos = new Vector3(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            } catch (NumberFormatException e1) {
                sender.sendMessage(new TranslatedMessage("commands.generic.usage", this.usageMessage));
                return true;
            }
        } else {
            sender.sendMessage(new TranslatedMessage("commands.generic.usage", this.usageMessage));
            return true;
        }
        level.setSpawnLocation(pos);
        DecimalFormat round2 = new DecimalFormat("##0.00");
        Command.broadcastCommandMessage(sender, new TranslatedMessage("commands.setworldspawn.success", new String[]{
                round2.format(pos.x),
                round2.format(pos.y),
                round2.format(pos.z)
        }));
        return true;
    }
}
