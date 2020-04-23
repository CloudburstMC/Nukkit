package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import com.nukkitx.protocol.bedrock.packet.SetDifficultyPacket;

import java.util.ArrayList;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class DifficultyCommand extends VanillaCommand {

    public DifficultyCommand(String name) {
        super(name, "commands.difficulty.description", "commands.difficulty.usage");
        this.setPermission("nukkit.command.difficulty");
        this.commandParameters.clear();
        this.commandParameters.add(new CommandParameter[]{
                new CommandParameter("difficulty", CommandParamType.INT, false)
        });
        this.commandParameters.add(new CommandParameter[]{
                new CommandParameter("difficulty", new String[]{"peaceful", "p", "easy", "e",
                        "normal", "n", "hard", "h"})
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        int difficulty = Server.getDifficultyFromString(args[0]);

        if (sender.getServer().isHardcore()) {
            difficulty = 3;
        }

        if (difficulty != -1) {
            sender.getServer().setPropertyInt("difficulty", difficulty);

            SetDifficultyPacket packet = new SetDifficultyPacket();
            packet.setDifficulty(sender.getServer().getDifficulty());
            Server.broadcastPacket(new ArrayList<>(sender.getServer().getOnlinePlayers().values()), packet);

            CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("commands.difficulty.success", String.valueOf(difficulty)));
        } else {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));

            return false;
        }

        return true;
    }
}
