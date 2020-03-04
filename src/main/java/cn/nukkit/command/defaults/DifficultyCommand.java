package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.protocol.SetDifficultyPacket;
import com.mojang.brigadier.CommandDispatcher;

import java.util.ArrayList;

public class DifficultyCommand extends BaseCommand {
    private final String[] DIFFICULTY_VALUES = new String[]{"peaceful", "p", "easy", "e", "normal", "n", "hard", "h"};

    public DifficultyCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("difficulty", "%nukkit.command.difficulty.description");
//        this.setPermission("nukkit.command.difficulty");

//        dispatcher.register(literal("difficulty")
//                .requires(requirePermission("nukkit.command.difficulty"))
//                .then(argument("difficulty", simpleEnum(DIFFICULTY_VALUES)).executes(this::run)));
//        this.commandParameters.clear();
//        this.commandParameters.put("default", new CommandParameter[]{
//                new CommandParameter("difficulty", CommandParamType.INT, false)
//        });
//        this.commandParameters.put("byString", new CommandParameter[]{
//                new CommandParameter("difficulty", new String[]{"peaceful", "p", "easy", "e",
//                        "normal", "n", "hard", "h"})
//        });
    }
//
//    @Override
//    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
//        if (!this.testPermission(sender)) {
//            return true;
//        }
//
//        if (args.length != 1) {
//            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
//            return false;
//        }
//
//        int difficulty = Server.getDifficultyFromString(args[0]);
//
//        if (sender.getServer().isHardcore()) {
//            difficulty = 3;
//        }
//
//        if (difficulty != -1) {
//            sender.getServer().setPropertyInt("difficulty", difficulty);
//
//            SetDifficultyPacket pk = new SetDifficultyPacket();
//            pk.difficulty = sender.getServer().getDifficulty();
//            Server.broadcastPacket(new ArrayList<>(sender.getServer().getOnlinePlayers().values()), pk);
//
//            Command.broadcastCommandMessage(sender, new TranslationContainer("commands.difficulty.success", String.valueOf(difficulty)));
//        } else {
//            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
//
//            return false;
//        }
//
//        return true;
//    }
}
