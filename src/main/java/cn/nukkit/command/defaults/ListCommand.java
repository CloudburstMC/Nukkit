package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class ListCommand extends VanillaCommand {

    public ListCommand(String name) {
        super(name, "commands.list.description", "/list");
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

        sender.sendMessage(new TranslationContainer("commands.players.list",
                onlineCount, sender.getServer().getMaxPlayers()));
        sender.sendMessage(online);
        return true;
    }
}
