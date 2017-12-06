package cn.nukkit.server.command.defaults;

import cn.nukkit.api.message.TranslatedMessage;
import cn.nukkit.server.Player;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.server.command.defaults in project Nukkit .
 */
public class ListCommand extends VanillaCommand {

    public ListCommand(String name) {
        super(name, "%nukkit.command.list.description", "%commands.players.usage");
        this.setPermission("nukkit.command.list");
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        String online = "";
        int onlineCount = 0;
        for (Player player : sender.getServer().getOnlinePlayers().values()) {
            if (player.isOnline() && (!(sender instanceof Player) || ((Player) sender).canSee(player))) {
                online += player.getDisplayName() + ", ";
                ++onlineCount;
            }
        }

        if (online.length() > 0) {
            online = online.substring(0, online.length() - 2);
        }

        sender.sendMessage(new TranslatedMessage("commands.players.list",
                new String[]{String.valueOf(onlineCount), String.valueOf(sender.getServer().getMaxPlayers())}));
        sender.sendMessage(online);
        return true;
    }
}
