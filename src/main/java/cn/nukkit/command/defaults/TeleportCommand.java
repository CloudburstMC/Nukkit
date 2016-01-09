package cn.nukkit.command.defaults;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;


/**
 * Created on 2015/11/12 by milkice.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class TeleportCommand extends VanillaCommand {
    public TeleportCommand(String name) {
            super(name, "%nukkit.command.tp.description", "%commands.tp.usage");
            this.setPermission("nukkit.command.tp");
    }
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {

        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length < 1 || args.length > 6) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }
        //Player player = sender.getServer().getPlayer(args[0]);
        /*
        double x=0;
        double y=0;
        double z=0;
        Player origin=null;
        Player destination=null;
        if (sender.getServer().getPlayer(args[0]) != null) {//tp Player x x x ....
            if (args.length == 1) {//tp <player2>
                if (sender.isPlayer()) {
                    origin = sender.getServer().getPlayer(sender.getName());
                    destination = sender.getServer().getPlayer(args[0]);
                } else {
                    sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                    return true;
                }
            } else if (args.length == 2) {//tp <player1> <player2>
                origin = sender.getServer().getPlayer(args[0]);
                destination = sender.getServer().getPlayer(args[1]);
                if (destination == null) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                    return true;
                }
            } else{ //tp <player1> <x> <y> <z>
                origin = sender.getServer().getPlayer(args[0]);
                try {
                    x = Double.valueOf(args[1]);
                    y = Double.valueOf(args[2]);
                    z = Double.valueOf(args[3]);
                } catch (Exception e) {
                    sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                    return true;
                }
                //TODO:Add y-rot and x-rot
            }
        }
        else{//tp <x> <y> <z>
            if(!sender.isPlayer()){
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                return true;
            }
            origin = sender.getServer().getPlayer(sender.getName());
            try {
                x = Double.valueOf(args[1]);
                y = Double.valueOf(args[2]);
                z = Double.valueOf(args[3]);
            } catch (Exception e) {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                return true;
            }
            //TODO:Add y-rot and x-rot
        }
        */



        Player origin=sender.getServer().getPlayer(args[0]);
        if(origin==null){
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
            return true;
        }
        if(args.length==2){
            Player destination=sender.getServer().getPlayer(args[1]);
            if(destination==null){
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                return true;
            }
            origin.teleport(destination);
        }
        else{
            try {
                Double x = Double.valueOf(args[1]);
                Double y = Double.valueOf(args[2]);
                Double z = Double.valueOf(args[3]);
                Double y_rot,x_rot;
                if(args.length==6){
                    y_rot=Double.valueOf(args[4]);
                    x_rot=Double.valueOf(args[5]);
                    origin.teleport(new Vector3(x,y,z),y_rot,x_rot);
                }
                else {
                    origin.teleport(new Vector3(x, y, z));
                }
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                return true;
            }
        }

        Command.broadcastCommandMessage(sender, new TranslationContainer(
                "%commands.tp.success",
                new String[]{
                        origin.getName(),
                        "(" + Double.valueOf(args[1]) + "," + Double.valueOf(args[2]) + "," + Double.valueOf(args[3]) + ")"
                }
        ));

        return true;

    }

}
