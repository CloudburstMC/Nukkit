package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandUtils;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import com.nukkitx.protocol.bedrock.packet.SetDifficultyPacket;

import java.util.ArrayList;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class DifficultyCommand extends Command {

    public DifficultyCommand() {
        super("difficulty", CommandData.builder("difficulty")
                .setDescription("commands.difficulty.description")
                .setUsageMessage("commands.difficulty.usage")
                .setPermissions("nukkit.command.difficulty")
                .setParameters(new CommandParameter[]{
                        new CommandParameter("difficulty", CommandParamType.INT, false)
                }, new CommandParameter[]{
                        new CommandParameter("difficulty", new String[]{"peaceful", "p", "easy", "e",
                                "normal", "n", "hard", "h"})
                })
                .build());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length != 1) {
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

            CommandUtils.broadcastCommandMessage(sender, new TranslationContainer("%commands.difficulty.success", String.valueOf(difficulty)));
        } else {
            return false;
        }

        return true;
    }
}
