package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.permission.BanEntry;
import cn.nukkit.permission.BanList;

import java.util.Map;
import java.util.Objects;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class BanListCommand extends VanillaCommand {
    public BanListCommand(String name) {
        super(name, "%nukkit.command.banlist.description", "%commands.banlist.usage");
        this.setPermission("nukkit.command.ban.list"); //好像没在DefaultPermissions里面找到？
    }

    private enum BanListRequestType {PLAYERS, IPS, INVALID}

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(!this.testPermission(sender)){
            return true;
        }

        BanListRequestType reqType;
        BanList banList;

        if(args == null) reqType = BanListRequestType.PLAYERS;
        else if(args.length != 1)reqType = BanListRequestType.INVALID;
        else if(Objects.equals(args[0], "ips")) reqType = BanListRequestType.IPS;
        else if(Objects.equals(args[0], "players")) reqType = BanListRequestType.PLAYERS;
        else reqType = BanListRequestType.INVALID;

        switch (reqType){
            case PLAYERS:banList = sender.getServer().getNameBans();break;
            case IPS:banList = sender.getServer().getIPBans();break;
            default:
                sender.sendMessage(new TranslationContainer("commands.generic.usage", new String[]{usageMessage}));
                return false;
        }

        final String[] message = {""};
        Map<String, BanEntry> banEntries = banList.getEntires();
        banEntries.forEach((s, b)-> message[0] += (b.getName()+","));
        String sizeString = String.valueOf(banList.getEntires().size());

        switch (reqType){
            case PLAYERS:sender.sendMessage(new TranslationContainer("commands.banlist.players", new String[]{sizeString}));break;
            case IPS:sender.sendMessage(new TranslationContainer("commands.banlist.ips", new String[]{sizeString}));break;
        }

        sender.sendMessage(message[0]);
        return true;
    }
}
