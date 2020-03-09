package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.locale.TranslationContainer;
import com.nukkitx.protocol.bedrock.packet.SetDifficultyPacket;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import java.util.ArrayList;

import static cn.nukkit.command.args.DifficultyArgument.difficulty;
import static cn.nukkit.command.args.DifficultyArgument.getDifficulty;

public class DifficultyCommand extends BaseCommand {

    public DifficultyCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("difficulty", "commands.difficulty.description");

        dispatcher.register(literal("difficulty")
                .requires(requirePermission("nukkit.command.difficulty"))
                .then(argument("difficulty", difficulty()).executes(this::run)));
    }

    public int run(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        String difficulty = getDifficulty(context, "difficulty");
        int difficultyInt = Server.getDifficultyFromString(difficulty);

        if(source.getServer().isHardcore()) {
            difficultyInt = 3;
        }

        source.getServer().setPropertyInt("difficulty", difficultyInt);

        SetDifficultyPacket pk = new SetDifficultyPacket();
        pk.setDifficulty(source.getServer().getDifficulty());
        Server.broadcastPacket(source.getServer().getOnlinePlayers().values(), pk);

        sendAdminMessage(source, new TranslationContainer("commands.difficulty.success", getDifficultyName(difficultyInt).toUpperCase()));
        return 1;
    }

    private String getDifficultyName(int difficulty) {
        switch(difficulty) {
            case 0:
                return "peaceful";
            case 1:
                return "easy";
            case 2:
                return "normal";
            case 3:
                return "hard";
        }
        return "unknown";
    }
}
