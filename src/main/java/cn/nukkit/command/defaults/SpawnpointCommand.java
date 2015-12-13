package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;

import java.text.DecimalFormat;

/**
 * Created on 2015/12/13 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class SpawnpointCommand extends VanillaCommand {
    public SpawnpointCommand(String name) {
        super(name, "%nukkit.command.spawnpoint.description", "%commands.spawnpoint.usage");
        this.setPermission("nukkit.command.spawnpoint");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(!this.testPermission(sender)){
            return true;
        }
        Player target;
        if(args.length == 0){
            if(sender instanceof Player){
                target = (Player) sender;
            }else{
                sender.sendMessage(TextFormat.RED + "Please provide a player!");
                return true;
            }
        }else{
            target = sender.getServer().getPlayer(args[0]);
            if(target == null){
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                return true;
            }
        }
        Level level = target.getLevel();
        DecimalFormat round2 = new DecimalFormat("##0.00");
        if(args.length == 4){
            if(level != null){
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
                if (y > 128) y = 128;
                target.setSpawn(new Position(x, y, z, level));
                Command.broadcastCommandMessage(sender, new TranslationContainer("commands.spawnpoint.success",new String[]{
                        target.getName(),
                        round2.format(x),
                        round2.format(y),
                        round2.format(z)
                }));
                return true;
            }
        }else if(args.length <= 1){
            if(sender instanceof Player){
                Position pos = (Position) sender;
                target.setSpawn(pos);
                Command.broadcastCommandMessage(sender, new TranslationContainer("commands.spawnpoint.success", new String[]{
                        target.getName(),
                        round2.format(pos.x),
                        round2.format(pos.y),
                        round2.format(pos.z)
                }));
                return true;
            }else{
                sender.sendMessage(TextFormat.RED + "Please provide a player!");
                return true;
            }
        }
        sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
        return true;
    }
}
