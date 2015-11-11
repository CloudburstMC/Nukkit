package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.utils.TextFormat;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class KickCommand extends VanillaCommand {

    public KickCommand(String name) {
        super(name, "%nukkit.command.kick.description", "%commands.kick.usage");
        this.setPermission("nukkit.command.kick");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(!this.testPermission(sender)){
            return true;
        }
        if(args.length == 0){
            sender.sendMessage(new TranslationContainer("commands.generic.usage", new String[]{this.usageMessage}));
            return false;
        }
        String name = args[0];
        String reason;
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) sb.append(args[i]);
        reason = sb.toString();
        Player player = sender.getServer().getPlayer(name);
        if(player != null){ //player instanceof Player
            player.kick(reason);
            if(reason.length() >= 1){
                Command.broadcastCommandMessage(sender,
                        new TranslationContainer("commands.kick.success.reason",new String[]{player.getName(), reason})
                );
            }else{
                Command.broadcastCommandMessage(sender,
                        new TranslationContainer("commands.kick.success",new String[]{player.getName()})
                );
            }
        }else{
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
        }
        return true;
    }
}
