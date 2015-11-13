package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.network.Network;
import cn.nukkit.network.protocol.SetDifficultyPacket;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class DifficultyCommand extends VanillaCommand {

    public DifficultyCommand(String name) {
        super(name, "%nukkit.command.difficulty.description", "%commands.difficulty.usage");
        this.setPermission("nukkit.command.difficulty");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", new String[]{this.usageMessage}));
            return false;
        }
        int difficulty = Server.getDifficultyFromString(args[0]);
        if (sender.getServer().isHardcore()) {
            difficulty = 3;
        }
        if (difficulty != -1) {
            sender.getServer().setPropertyInt("difficulty", difficulty);
            SetDifficultyPacket pk = new SetDifficultyPacket();
            pk.difficulty = sender.getServer().getDifficulty();
            pk.setChannel(Network.CHANNEL_WORLD_EVENTS);
            Collection<Player> players = new ArrayList<>();
            sender.getServer().getOnlinePlayers().forEach((s, p) -> players.add(p));
            Server.broadcastPacket(players, pk);
            String difficultyString = String.valueOf(difficulty);
            Command.broadcastCommandMessage(sender,
                    new TranslationContainer("commands.difficulty.success", new String[]{difficultyString})
            );
        } else {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", new String[]{this.usageMessage}));
            return false;
        }
        return true;
    }
}
