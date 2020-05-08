package cn.nukkit.command.defaults.vanilla;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.defaults.VanillaCommand;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created on 2015/11/11 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class ListCommand extends VanillaCommand {

    public ListCommand(String name) {
        super(name, "commands.list.description", "/list");
        this.setPermission("nukkit.command.list");
    }

    @Override
    public boolean execute(CommandSender sender, String aliasUsed, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        Collection<Player> players = sender.getServer().getOnlinePlayers().values();

        players.forEach(player -> {
            if(!player.isOnline() || (sender instanceof Player && !((Player) sender).canSee(player))) {
                players.remove(player);
            }
        });

        sender.sendMessage(new TranslationContainer("commands.players.list", players.size(), sender.getServer().getMaxPlayers()));
        sender.sendMessage(String.join(", ", players.stream().map(Player::getDisplayName).toArray(String[]::new)));
        return true;
    }
}
