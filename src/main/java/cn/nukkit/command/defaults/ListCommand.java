package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;

import java.util.StringJoiner;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class ListCommand extends Command {

    public ListCommand() {
        super("list", CommandData.builder("list")
                .setDescription("commands.list.description")
                .setUsageMessage("/list")
                .setPermissions("nukkit.command.list")
                .build());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        StringJoiner online = new StringJoiner(", ");
        int onlineCount = 0;
        for (Player player : sender.getServer().getOnlinePlayers().values()) {
            if (player.isOnline() && (!(sender instanceof Player) || ((Player) sender).canSee(player))) {
                online.add(player.getDisplayName());
                ++onlineCount;
            }
        }

        sender.sendMessage(new TranslationContainer("commands.players.list",
                onlineCount, sender.getServer().getMaxPlayers()));
        sender.sendMessage(online.toString());
        return true;
    }
}
