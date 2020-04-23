package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Level;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.player.Player;
import com.nukkitx.math.vector.Vector3f;

/**
 * Created on 2015/12/13 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class SetWorldSpawnCommand extends VanillaCommand {
    public SetWorldSpawnCommand(String name) {
        super(name, "commands.setworldspawn.description", "/setworldspawn <position>");
        this.setPermission("nukkit.command.setworldspawn");
        this.commandParameters.clear();
        this.commandParameters.add(new CommandParameter[]{
                new CommandParameter("blockPos", CommandParamType.POSITION, true)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        Level level;
        Vector3f pos;
        if (args.length == 0) {
            if (sender instanceof Player) {
                level = ((Player) sender).getLevel();
                pos = ((Player) sender).getPosition();
            } else {
                sender.sendMessage(new TranslationContainer("commands.locate.fail.noplayer"));
                return true;
            }
        } else if (args.length == 3) {
            level = sender.getServer().getDefaultLevel();
            try {
                pos = Vector3f.from(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            } catch (NumberFormatException e1) {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                return true;
            }
        } else {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return true;
        }
        level.setSpawnLocation(pos);

        CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("commands.setworldspawn.success", NukkitMath.round(pos.getX(), 2),
                NukkitMath.round(pos.getY(), 2), NukkitMath.round(pos.getZ(), 2)));
        return true;
    }
}
